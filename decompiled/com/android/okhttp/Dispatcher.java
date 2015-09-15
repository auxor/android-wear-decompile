package com.android.okhttp;

import com.android.okhttp.Response.Receiver;
import com.android.okhttp.internal.Util;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Dispatcher {
    private ExecutorService executorService;
    private int maxRequests;
    private int maxRequestsPerHost;
    private final Deque<Job> readyJobs;
    private final Deque<Job> runningJobs;

    public Dispatcher(ExecutorService executorService) {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyJobs = new ArrayDeque();
        this.runningJobs = new ArrayDeque();
        this.executorService = executorService;
    }

    public Dispatcher() {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyJobs = new ArrayDeque();
        this.runningJobs = new ArrayDeque();
    }

    public synchronized ExecutorService getExecutorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        return this.executorService;
    }

    public synchronized void setMaxRequests(int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        this.maxRequests = maxRequests;
        promoteJobs();
    }

    public synchronized int getMaxRequests() {
        return this.maxRequests;
    }

    public synchronized void setMaxRequestsPerHost(int maxRequestsPerHost) {
        if (maxRequestsPerHost < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
        }
        this.maxRequestsPerHost = maxRequestsPerHost;
        promoteJobs();
    }

    public synchronized int getMaxRequestsPerHost() {
        return this.maxRequestsPerHost;
    }

    synchronized void enqueue(OkHttpClient client, Request request, Receiver receiver) {
        Job job = new Job(this, client.copyWithDefaults(), request, receiver);
        if (this.runningJobs.size() >= this.maxRequests || runningJobsForHost(job) >= this.maxRequestsPerHost) {
            this.readyJobs.add(job);
        } else {
            this.runningJobs.add(job);
            getExecutorService().execute(job);
        }
    }

    public synchronized void cancel(Object tag) {
        Iterator<Job> i = this.readyJobs.iterator();
        while (i.hasNext()) {
            if (Util.equal(tag, ((Job) i.next()).tag())) {
                i.remove();
            }
        }
        for (Job job : this.runningJobs) {
            if (Util.equal(tag, job.tag())) {
                job.canceled = true;
            }
        }
    }

    synchronized void finished(Job job) {
        if (this.runningJobs.remove(job)) {
            promoteJobs();
        } else {
            throw new AssertionError("Job wasn't running!");
        }
    }

    private void promoteJobs() {
        if (this.runningJobs.size() < this.maxRequests && !this.readyJobs.isEmpty()) {
            Iterator<Job> i = this.readyJobs.iterator();
            while (i.hasNext()) {
                Job job = (Job) i.next();
                if (runningJobsForHost(job) < this.maxRequestsPerHost) {
                    i.remove();
                    this.runningJobs.add(job);
                    getExecutorService().execute(job);
                }
                if (this.runningJobs.size() >= this.maxRequests) {
                    return;
                }
            }
        }
    }

    private int runningJobsForHost(Job job) {
        int result = 0;
        for (Job j : this.runningJobs) {
            if (j.host().equals(job.host())) {
                result++;
            }
        }
        return result;
    }
}
