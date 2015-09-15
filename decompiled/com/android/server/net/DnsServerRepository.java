package com.android.server.net;

import android.net.LinkProperties;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* compiled from: NetlinkTracker */
class DnsServerRepository {
    public static final int NUM_CURRENT_SERVERS = 3;
    public static final int NUM_SERVERS = 12;
    public static final String TAG = "DnsServerRepository";
    private ArrayList<DnsServerEntry> mAllServers;
    private Set<InetAddress> mCurrentServers;
    private HashMap<InetAddress, DnsServerEntry> mIndex;

    public DnsServerRepository() {
        this.mCurrentServers = new HashSet();
        this.mAllServers = new ArrayList(NUM_SERVERS);
        this.mIndex = new HashMap(NUM_SERVERS);
    }

    public synchronized void setDnsServersOn(LinkProperties lp) {
        lp.setDnsServers(this.mCurrentServers);
    }

    public synchronized boolean addServers(long lifetime, String[] addresses) {
        long now = System.currentTimeMillis();
        long expiry = now + (1000 * lifetime);
        for (String addressString : addresses) {
            try {
                InetAddress address = InetAddress.parseNumericAddress(addressString);
                if (!updateExistingEntry(address, expiry) && expiry > now) {
                    DnsServerEntry entry = new DnsServerEntry(address, expiry);
                    this.mAllServers.add(entry);
                    this.mIndex.put(address, entry);
                }
            } catch (IllegalArgumentException e) {
            }
        }
        Collections.sort(this.mAllServers);
        return updateCurrentServers();
    }

    private synchronized boolean updateExistingEntry(InetAddress address, long expiry) {
        boolean z;
        DnsServerEntry existing = (DnsServerEntry) this.mIndex.get(address);
        if (existing != null) {
            existing.expiry = expiry;
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    private synchronized boolean updateCurrentServers() {
        boolean changed;
        long now = System.currentTimeMillis();
        changed = false;
        int i = this.mAllServers.size() - 1;
        while (i >= 0 && (i >= NUM_SERVERS || ((DnsServerEntry) this.mAllServers.get(i)).expiry < now)) {
            DnsServerEntry removed = (DnsServerEntry) this.mAllServers.remove(i);
            this.mIndex.remove(removed.address);
            changed |= this.mCurrentServers.remove(removed.address);
            i--;
        }
        Iterator i$ = this.mAllServers.iterator();
        while (i$.hasNext()) {
            DnsServerEntry entry = (DnsServerEntry) i$.next();
            if (this.mCurrentServers.size() >= NUM_CURRENT_SERVERS) {
                break;
            }
            changed |= this.mCurrentServers.add(entry.address);
        }
        return changed;
    }
}
