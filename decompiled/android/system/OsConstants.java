package android.system;

import org.w3c.dom.traversal.NodeFilter;

public final class OsConstants {
    public static final int AF_INET;
    public static final int AF_INET6;
    public static final int AF_UNIX;
    public static final int AF_UNSPEC;
    public static final int AI_ADDRCONFIG;
    public static final int AI_ALL;
    public static final int AI_CANONNAME;
    public static final int AI_NUMERICHOST;
    public static final int AI_NUMERICSERV;
    public static final int AI_PASSIVE;
    public static final int AI_V4MAPPED;
    public static final int CAP_AUDIT_CONTROL;
    public static final int CAP_AUDIT_WRITE;
    public static final int CAP_BLOCK_SUSPEND;
    public static final int CAP_CHOWN;
    public static final int CAP_DAC_OVERRIDE;
    public static final int CAP_DAC_READ_SEARCH;
    public static final int CAP_FOWNER;
    public static final int CAP_FSETID;
    public static final int CAP_IPC_LOCK;
    public static final int CAP_IPC_OWNER;
    public static final int CAP_KILL;
    public static final int CAP_LAST_CAP;
    public static final int CAP_LEASE;
    public static final int CAP_LINUX_IMMUTABLE;
    public static final int CAP_MAC_ADMIN;
    public static final int CAP_MAC_OVERRIDE;
    public static final int CAP_MKNOD;
    public static final int CAP_NET_ADMIN;
    public static final int CAP_NET_BIND_SERVICE;
    public static final int CAP_NET_BROADCAST;
    public static final int CAP_NET_RAW;
    public static final int CAP_SETFCAP;
    public static final int CAP_SETGID;
    public static final int CAP_SETPCAP;
    public static final int CAP_SETUID;
    public static final int CAP_SYSLOG;
    public static final int CAP_SYS_ADMIN;
    public static final int CAP_SYS_BOOT;
    public static final int CAP_SYS_CHROOT;
    public static final int CAP_SYS_MODULE;
    public static final int CAP_SYS_NICE;
    public static final int CAP_SYS_PACCT;
    public static final int CAP_SYS_PTRACE;
    public static final int CAP_SYS_RAWIO;
    public static final int CAP_SYS_RESOURCE;
    public static final int CAP_SYS_TIME;
    public static final int CAP_SYS_TTY_CONFIG;
    public static final int CAP_WAKE_ALARM;
    public static final int E2BIG;
    public static final int EACCES;
    public static final int EADDRINUSE;
    public static final int EADDRNOTAVAIL;
    public static final int EAFNOSUPPORT;
    public static final int EAGAIN;
    public static final int EAI_AGAIN;
    public static final int EAI_BADFLAGS;
    public static final int EAI_FAIL;
    public static final int EAI_FAMILY;
    public static final int EAI_MEMORY;
    public static final int EAI_NODATA;
    public static final int EAI_NONAME;
    public static final int EAI_OVERFLOW;
    public static final int EAI_SERVICE;
    public static final int EAI_SOCKTYPE;
    public static final int EAI_SYSTEM;
    public static final int EALREADY;
    public static final int EBADF;
    public static final int EBADMSG;
    public static final int EBUSY;
    public static final int ECANCELED;
    public static final int ECHILD;
    public static final int ECONNABORTED;
    public static final int ECONNREFUSED;
    public static final int ECONNRESET;
    public static final int EDEADLK;
    public static final int EDESTADDRREQ;
    public static final int EDOM;
    public static final int EDQUOT;
    public static final int EEXIST;
    public static final int EFAULT;
    public static final int EFBIG;
    public static final int EHOSTUNREACH;
    public static final int EIDRM;
    public static final int EILSEQ;
    public static final int EINPROGRESS;
    public static final int EINTR;
    public static final int EINVAL;
    public static final int EIO;
    public static final int EISCONN;
    public static final int EISDIR;
    public static final int ELOOP;
    public static final int EMFILE;
    public static final int EMLINK;
    public static final int EMSGSIZE;
    public static final int EMULTIHOP;
    public static final int ENAMETOOLONG;
    public static final int ENETDOWN;
    public static final int ENETRESET;
    public static final int ENETUNREACH;
    public static final int ENFILE;
    public static final int ENOBUFS;
    public static final int ENODATA;
    public static final int ENODEV;
    public static final int ENOENT;
    public static final int ENOEXEC;
    public static final int ENOLCK;
    public static final int ENOLINK;
    public static final int ENOMEM;
    public static final int ENOMSG;
    public static final int ENOPROTOOPT;
    public static final int ENOSPC;
    public static final int ENOSR;
    public static final int ENOSTR;
    public static final int ENOSYS;
    public static final int ENOTCONN;
    public static final int ENOTDIR;
    public static final int ENOTEMPTY;
    public static final int ENOTSOCK;
    public static final int ENOTSUP;
    public static final int ENOTTY;
    public static final int ENXIO;
    public static final int EOPNOTSUPP;
    public static final int EOVERFLOW;
    public static final int EPERM;
    public static final int EPIPE;
    public static final int EPROTO;
    public static final int EPROTONOSUPPORT;
    public static final int EPROTOTYPE;
    public static final int ERANGE;
    public static final int EROFS;
    public static final int ESPIPE;
    public static final int ESRCH;
    public static final int ESTALE;
    public static final int ETIME;
    public static final int ETIMEDOUT;
    public static final int ETXTBSY;
    public static final int EXDEV;
    public static final int EXIT_FAILURE;
    public static final int EXIT_SUCCESS;
    public static final int FD_CLOEXEC;
    public static final int FIONREAD;
    public static final int F_DUPFD;
    public static final int F_GETFD;
    public static final int F_GETFL;
    public static final int F_GETLK;
    public static final int F_GETLK64;
    public static final int F_GETOWN;
    public static final int F_OK;
    public static final int F_RDLCK;
    public static final int F_SETFD;
    public static final int F_SETFL;
    public static final int F_SETLK;
    public static final int F_SETLK64;
    public static final int F_SETLKW;
    public static final int F_SETLKW64;
    public static final int F_SETOWN;
    public static final int F_UNLCK;
    public static final int F_WRLCK;
    public static final int IFA_F_DADFAILED;
    public static final int IFA_F_DEPRECATED;
    public static final int IFA_F_HOMEADDRESS;
    public static final int IFA_F_NODAD;
    public static final int IFA_F_OPTIMISTIC;
    public static final int IFA_F_PERMANENT;
    public static final int IFA_F_SECONDARY;
    public static final int IFA_F_TEMPORARY;
    public static final int IFA_F_TENTATIVE;
    public static final int IFF_ALLMULTI;
    public static final int IFF_AUTOMEDIA;
    public static final int IFF_BROADCAST;
    public static final int IFF_DEBUG;
    public static final int IFF_DYNAMIC;
    public static final int IFF_LOOPBACK;
    public static final int IFF_MASTER;
    public static final int IFF_MULTICAST;
    public static final int IFF_NOARP;
    public static final int IFF_NOTRAILERS;
    public static final int IFF_POINTOPOINT;
    public static final int IFF_PORTSEL;
    public static final int IFF_PROMISC;
    public static final int IFF_RUNNING;
    public static final int IFF_SLAVE;
    public static final int IFF_UP;
    public static final int IPPROTO_ICMP;
    public static final int IPPROTO_ICMPV6;
    public static final int IPPROTO_IP;
    public static final int IPPROTO_IPV6;
    public static final int IPPROTO_RAW;
    public static final int IPPROTO_TCP;
    public static final int IPPROTO_UDP;
    public static final int IPV6_CHECKSUM;
    public static final int IPV6_MULTICAST_HOPS;
    public static final int IPV6_MULTICAST_IF;
    public static final int IPV6_MULTICAST_LOOP;
    public static final int IPV6_RECVDSTOPTS;
    public static final int IPV6_RECVHOPLIMIT;
    public static final int IPV6_RECVHOPOPTS;
    public static final int IPV6_RECVPKTINFO;
    public static final int IPV6_RECVRTHDR;
    public static final int IPV6_RECVTCLASS;
    public static final int IPV6_TCLASS;
    public static final int IPV6_UNICAST_HOPS;
    public static final int IPV6_V6ONLY;
    public static final int IP_MULTICAST_IF;
    public static final int IP_MULTICAST_LOOP;
    public static final int IP_MULTICAST_TTL;
    public static final int IP_TOS;
    public static final int IP_TTL;
    public static final int MAP_FIXED;
    public static final int MAP_PRIVATE;
    public static final int MAP_SHARED;
    public static final int MCAST_BLOCK_SOURCE;
    public static final int MCAST_JOIN_GROUP;
    public static final int MCAST_JOIN_SOURCE_GROUP;
    public static final int MCAST_LEAVE_GROUP;
    public static final int MCAST_LEAVE_SOURCE_GROUP;
    public static final int MCAST_UNBLOCK_SOURCE;
    public static final int MCL_CURRENT;
    public static final int MCL_FUTURE;
    public static final int MSG_CTRUNC;
    public static final int MSG_DONTROUTE;
    public static final int MSG_EOR;
    public static final int MSG_OOB;
    public static final int MSG_PEEK;
    public static final int MSG_TRUNC;
    public static final int MSG_WAITALL;
    public static final int MS_ASYNC;
    public static final int MS_INVALIDATE;
    public static final int MS_SYNC;
    public static final int NI_DGRAM;
    public static final int NI_NAMEREQD;
    public static final int NI_NOFQDN;
    public static final int NI_NUMERICHOST;
    public static final int NI_NUMERICSERV;
    public static final int O_ACCMODE;
    public static final int O_APPEND;
    public static final int O_CREAT;
    public static final int O_EXCL;
    public static final int O_NOCTTY;
    public static final int O_NOFOLLOW;
    public static final int O_NONBLOCK;
    public static final int O_RDONLY;
    public static final int O_RDWR;
    public static final int O_SYNC;
    public static final int O_TRUNC;
    public static final int O_WRONLY;
    public static final int POLLERR;
    public static final int POLLHUP;
    public static final int POLLIN;
    public static final int POLLNVAL;
    public static final int POLLOUT;
    public static final int POLLPRI;
    public static final int POLLRDBAND;
    public static final int POLLRDNORM;
    public static final int POLLWRBAND;
    public static final int POLLWRNORM;
    public static final int PROT_EXEC;
    public static final int PROT_NONE;
    public static final int PROT_READ;
    public static final int PROT_WRITE;
    public static final int PR_GET_DUMPABLE;
    public static final int PR_SET_DUMPABLE;
    public static final int PR_SET_NO_NEW_PRIVS;
    public static final int RT_SCOPE_HOST;
    public static final int RT_SCOPE_LINK;
    public static final int RT_SCOPE_NOWHERE;
    public static final int RT_SCOPE_SITE;
    public static final int RT_SCOPE_UNIVERSE;
    public static final int R_OK;
    public static final int SEEK_CUR;
    public static final int SEEK_END;
    public static final int SEEK_SET;
    public static final int SHUT_RD;
    public static final int SHUT_RDWR;
    public static final int SHUT_WR;
    public static final int SIGABRT;
    public static final int SIGALRM;
    public static final int SIGBUS;
    public static final int SIGCHLD;
    public static final int SIGCONT;
    public static final int SIGFPE;
    public static final int SIGHUP;
    public static final int SIGILL;
    public static final int SIGINT;
    public static final int SIGIO;
    public static final int SIGKILL;
    public static final int SIGPIPE;
    public static final int SIGPROF;
    public static final int SIGPWR;
    public static final int SIGQUIT;
    public static final int SIGRTMAX;
    public static final int SIGRTMIN;
    public static final int SIGSEGV;
    public static final int SIGSTKFLT;
    public static final int SIGSTOP;
    public static final int SIGSYS;
    public static final int SIGTERM;
    public static final int SIGTRAP;
    public static final int SIGTSTP;
    public static final int SIGTTIN;
    public static final int SIGTTOU;
    public static final int SIGURG;
    public static final int SIGUSR1;
    public static final int SIGUSR2;
    public static final int SIGVTALRM;
    public static final int SIGWINCH;
    public static final int SIGXCPU;
    public static final int SIGXFSZ;
    public static final int SIOCGIFADDR;
    public static final int SIOCGIFBRDADDR;
    public static final int SIOCGIFDSTADDR;
    public static final int SIOCGIFNETMASK;
    public static final int SOCK_DGRAM;
    public static final int SOCK_RAW;
    public static final int SOCK_SEQPACKET;
    public static final int SOCK_STREAM;
    public static final int SOL_SOCKET;
    public static final int SO_BINDTODEVICE;
    public static final int SO_BROADCAST;
    public static final int SO_DEBUG;
    public static final int SO_DONTROUTE;
    public static final int SO_ERROR;
    public static final int SO_KEEPALIVE;
    public static final int SO_LINGER;
    public static final int SO_OOBINLINE;
    public static final int SO_PASSCRED;
    public static final int SO_PEERCRED;
    public static final int SO_RCVBUF;
    public static final int SO_RCVLOWAT;
    public static final int SO_RCVTIMEO;
    public static final int SO_REUSEADDR;
    public static final int SO_SNDBUF;
    public static final int SO_SNDLOWAT;
    public static final int SO_SNDTIMEO;
    public static final int SO_TYPE;
    public static final int STDERR_FILENO;
    public static final int STDIN_FILENO;
    public static final int STDOUT_FILENO;
    public static final int S_IFBLK;
    public static final int S_IFCHR;
    public static final int S_IFDIR;
    public static final int S_IFIFO;
    public static final int S_IFLNK;
    public static final int S_IFMT;
    public static final int S_IFREG;
    public static final int S_IFSOCK;
    public static final int S_IRGRP;
    public static final int S_IROTH;
    public static final int S_IRUSR;
    public static final int S_IRWXG;
    public static final int S_IRWXO;
    public static final int S_IRWXU;
    public static final int S_ISGID;
    public static final int S_ISUID;
    public static final int S_ISVTX;
    public static final int S_IWGRP;
    public static final int S_IWOTH;
    public static final int S_IWUSR;
    public static final int S_IXGRP;
    public static final int S_IXOTH;
    public static final int S_IXUSR;
    public static final int TCP_NODELAY;
    public static final int WCONTINUED;
    public static final int WEXITED;
    public static final int WNOHANG;
    public static final int WNOWAIT;
    public static final int WSTOPPED;
    public static final int WUNTRACED;
    public static final int W_OK;
    public static final int X_OK;
    public static final int _SC_2_CHAR_TERM;
    public static final int _SC_2_C_BIND;
    public static final int _SC_2_C_DEV;
    public static final int _SC_2_C_VERSION;
    public static final int _SC_2_FORT_DEV;
    public static final int _SC_2_FORT_RUN;
    public static final int _SC_2_LOCALEDEF;
    public static final int _SC_2_SW_DEV;
    public static final int _SC_2_UPE;
    public static final int _SC_2_VERSION;
    public static final int _SC_AIO_LISTIO_MAX;
    public static final int _SC_AIO_MAX;
    public static final int _SC_AIO_PRIO_DELTA_MAX;
    public static final int _SC_ARG_MAX;
    public static final int _SC_ASYNCHRONOUS_IO;
    public static final int _SC_ATEXIT_MAX;
    public static final int _SC_AVPHYS_PAGES;
    public static final int _SC_BC_BASE_MAX;
    public static final int _SC_BC_DIM_MAX;
    public static final int _SC_BC_SCALE_MAX;
    public static final int _SC_BC_STRING_MAX;
    public static final int _SC_CHILD_MAX;
    public static final int _SC_CLK_TCK;
    public static final int _SC_COLL_WEIGHTS_MAX;
    public static final int _SC_DELAYTIMER_MAX;
    public static final int _SC_EXPR_NEST_MAX;
    public static final int _SC_FSYNC;
    public static final int _SC_GETGR_R_SIZE_MAX;
    public static final int _SC_GETPW_R_SIZE_MAX;
    public static final int _SC_IOV_MAX;
    public static final int _SC_JOB_CONTROL;
    public static final int _SC_LINE_MAX;
    public static final int _SC_LOGIN_NAME_MAX;
    public static final int _SC_MAPPED_FILES;
    public static final int _SC_MEMLOCK;
    public static final int _SC_MEMLOCK_RANGE;
    public static final int _SC_MEMORY_PROTECTION;
    public static final int _SC_MESSAGE_PASSING;
    public static final int _SC_MQ_OPEN_MAX;
    public static final int _SC_MQ_PRIO_MAX;
    public static final int _SC_NGROUPS_MAX;
    public static final int _SC_NPROCESSORS_CONF;
    public static final int _SC_NPROCESSORS_ONLN;
    public static final int _SC_OPEN_MAX;
    public static final int _SC_PAGESIZE;
    public static final int _SC_PAGE_SIZE;
    public static final int _SC_PASS_MAX;
    public static final int _SC_PHYS_PAGES;
    public static final int _SC_PRIORITIZED_IO;
    public static final int _SC_PRIORITY_SCHEDULING;
    public static final int _SC_REALTIME_SIGNALS;
    public static final int _SC_RE_DUP_MAX;
    public static final int _SC_RTSIG_MAX;
    public static final int _SC_SAVED_IDS;
    public static final int _SC_SEMAPHORES;
    public static final int _SC_SEM_NSEMS_MAX;
    public static final int _SC_SEM_VALUE_MAX;
    public static final int _SC_SHARED_MEMORY_OBJECTS;
    public static final int _SC_SIGQUEUE_MAX;
    public static final int _SC_STREAM_MAX;
    public static final int _SC_SYNCHRONIZED_IO;
    public static final int _SC_THREADS;
    public static final int _SC_THREAD_ATTR_STACKADDR;
    public static final int _SC_THREAD_ATTR_STACKSIZE;
    public static final int _SC_THREAD_DESTRUCTOR_ITERATIONS;
    public static final int _SC_THREAD_KEYS_MAX;
    public static final int _SC_THREAD_PRIORITY_SCHEDULING;
    public static final int _SC_THREAD_PRIO_INHERIT;
    public static final int _SC_THREAD_PRIO_PROTECT;
    public static final int _SC_THREAD_SAFE_FUNCTIONS;
    public static final int _SC_THREAD_STACK_MIN;
    public static final int _SC_THREAD_THREADS_MAX;
    public static final int _SC_TIMERS;
    public static final int _SC_TIMER_MAX;
    public static final int _SC_TTY_NAME_MAX;
    public static final int _SC_TZNAME_MAX;
    public static final int _SC_VERSION;
    public static final int _SC_XBS5_ILP32_OFF32;
    public static final int _SC_XBS5_ILP32_OFFBIG;
    public static final int _SC_XBS5_LP64_OFF64;
    public static final int _SC_XBS5_LPBIG_OFFBIG;
    public static final int _SC_XOPEN_CRYPT;
    public static final int _SC_XOPEN_ENH_I18N;
    public static final int _SC_XOPEN_LEGACY;
    public static final int _SC_XOPEN_REALTIME;
    public static final int _SC_XOPEN_REALTIME_THREADS;
    public static final int _SC_XOPEN_SHM;
    public static final int _SC_XOPEN_UNIX;
    public static final int _SC_XOPEN_VERSION;
    public static final int _SC_XOPEN_XCU_VERSION;

    private static native void initConstants();

    private OsConstants() {
    }

    public static boolean S_ISBLK(int mode) {
        return (S_IFMT & mode) == S_IFBLK;
    }

    public static boolean S_ISCHR(int mode) {
        return (S_IFMT & mode) == S_IFCHR;
    }

    public static boolean S_ISDIR(int mode) {
        return (S_IFMT & mode) == S_IFDIR;
    }

    public static boolean S_ISFIFO(int mode) {
        return (S_IFMT & mode) == S_IFIFO;
    }

    public static boolean S_ISREG(int mode) {
        return (S_IFMT & mode) == S_IFREG;
    }

    public static boolean S_ISLNK(int mode) {
        return (S_IFMT & mode) == S_IFLNK;
    }

    public static boolean S_ISSOCK(int mode) {
        return (S_IFMT & mode) == S_IFSOCK;
    }

    public static int WEXITSTATUS(int status) {
        return (65280 & status) >> 8;
    }

    public static boolean WCOREDUMP(int status) {
        return (status & NodeFilter.SHOW_COMMENT) != 0;
    }

    public static int WTERMSIG(int status) {
        return status & Float.MAX_EXPONENT;
    }

    public static int WSTOPSIG(int status) {
        return WEXITSTATUS(status);
    }

    public static boolean WIFEXITED(int status) {
        return WTERMSIG(status) == 0;
    }

    public static boolean WIFSTOPPED(int status) {
        return WTERMSIG(status) == Float.MAX_EXPONENT;
    }

    public static boolean WIFSIGNALED(int status) {
        return WTERMSIG(status + 1) >= 2;
    }

    static {
        AF_INET = placeholder();
        AF_INET6 = placeholder();
        AF_UNIX = placeholder();
        AF_UNSPEC = placeholder();
        AI_ADDRCONFIG = placeholder();
        AI_ALL = placeholder();
        AI_CANONNAME = placeholder();
        AI_NUMERICHOST = placeholder();
        AI_NUMERICSERV = placeholder();
        AI_PASSIVE = placeholder();
        AI_V4MAPPED = placeholder();
        CAP_AUDIT_CONTROL = placeholder();
        CAP_AUDIT_WRITE = placeholder();
        CAP_BLOCK_SUSPEND = placeholder();
        CAP_CHOWN = placeholder();
        CAP_DAC_OVERRIDE = placeholder();
        CAP_DAC_READ_SEARCH = placeholder();
        CAP_FOWNER = placeholder();
        CAP_FSETID = placeholder();
        CAP_IPC_LOCK = placeholder();
        CAP_IPC_OWNER = placeholder();
        CAP_KILL = placeholder();
        CAP_LAST_CAP = placeholder();
        CAP_LEASE = placeholder();
        CAP_LINUX_IMMUTABLE = placeholder();
        CAP_MAC_ADMIN = placeholder();
        CAP_MAC_OVERRIDE = placeholder();
        CAP_MKNOD = placeholder();
        CAP_NET_ADMIN = placeholder();
        CAP_NET_BIND_SERVICE = placeholder();
        CAP_NET_BROADCAST = placeholder();
        CAP_NET_RAW = placeholder();
        CAP_SETFCAP = placeholder();
        CAP_SETGID = placeholder();
        CAP_SETPCAP = placeholder();
        CAP_SETUID = placeholder();
        CAP_SYS_ADMIN = placeholder();
        CAP_SYS_BOOT = placeholder();
        CAP_SYS_CHROOT = placeholder();
        CAP_SYSLOG = placeholder();
        CAP_SYS_MODULE = placeholder();
        CAP_SYS_NICE = placeholder();
        CAP_SYS_PACCT = placeholder();
        CAP_SYS_PTRACE = placeholder();
        CAP_SYS_RAWIO = placeholder();
        CAP_SYS_RESOURCE = placeholder();
        CAP_SYS_TIME = placeholder();
        CAP_SYS_TTY_CONFIG = placeholder();
        CAP_WAKE_ALARM = placeholder();
        E2BIG = placeholder();
        EACCES = placeholder();
        EADDRINUSE = placeholder();
        EADDRNOTAVAIL = placeholder();
        EAFNOSUPPORT = placeholder();
        EAGAIN = placeholder();
        EAI_AGAIN = placeholder();
        EAI_BADFLAGS = placeholder();
        EAI_FAIL = placeholder();
        EAI_FAMILY = placeholder();
        EAI_MEMORY = placeholder();
        EAI_NODATA = placeholder();
        EAI_NONAME = placeholder();
        EAI_OVERFLOW = placeholder();
        EAI_SERVICE = placeholder();
        EAI_SOCKTYPE = placeholder();
        EAI_SYSTEM = placeholder();
        EALREADY = placeholder();
        EBADF = placeholder();
        EBADMSG = placeholder();
        EBUSY = placeholder();
        ECANCELED = placeholder();
        ECHILD = placeholder();
        ECONNABORTED = placeholder();
        ECONNREFUSED = placeholder();
        ECONNRESET = placeholder();
        EDEADLK = placeholder();
        EDESTADDRREQ = placeholder();
        EDOM = placeholder();
        EDQUOT = placeholder();
        EEXIST = placeholder();
        EFAULT = placeholder();
        EFBIG = placeholder();
        EHOSTUNREACH = placeholder();
        EIDRM = placeholder();
        EILSEQ = placeholder();
        EINPROGRESS = placeholder();
        EINTR = placeholder();
        EINVAL = placeholder();
        EIO = placeholder();
        EISCONN = placeholder();
        EISDIR = placeholder();
        ELOOP = placeholder();
        EMFILE = placeholder();
        EMLINK = placeholder();
        EMSGSIZE = placeholder();
        EMULTIHOP = placeholder();
        ENAMETOOLONG = placeholder();
        ENETDOWN = placeholder();
        ENETRESET = placeholder();
        ENETUNREACH = placeholder();
        ENFILE = placeholder();
        ENOBUFS = placeholder();
        ENODATA = placeholder();
        ENODEV = placeholder();
        ENOENT = placeholder();
        ENOEXEC = placeholder();
        ENOLCK = placeholder();
        ENOLINK = placeholder();
        ENOMEM = placeholder();
        ENOMSG = placeholder();
        ENOPROTOOPT = placeholder();
        ENOSPC = placeholder();
        ENOSR = placeholder();
        ENOSTR = placeholder();
        ENOSYS = placeholder();
        ENOTCONN = placeholder();
        ENOTDIR = placeholder();
        ENOTEMPTY = placeholder();
        ENOTSOCK = placeholder();
        ENOTSUP = placeholder();
        ENOTTY = placeholder();
        ENXIO = placeholder();
        EOPNOTSUPP = placeholder();
        EOVERFLOW = placeholder();
        EPERM = placeholder();
        EPIPE = placeholder();
        EPROTO = placeholder();
        EPROTONOSUPPORT = placeholder();
        EPROTOTYPE = placeholder();
        ERANGE = placeholder();
        EROFS = placeholder();
        ESPIPE = placeholder();
        ESRCH = placeholder();
        ESTALE = placeholder();
        ETIME = placeholder();
        ETIMEDOUT = placeholder();
        ETXTBSY = placeholder();
        EXDEV = placeholder();
        EXIT_FAILURE = placeholder();
        EXIT_SUCCESS = placeholder();
        FD_CLOEXEC = placeholder();
        FIONREAD = placeholder();
        F_DUPFD = placeholder();
        F_GETFD = placeholder();
        F_GETFL = placeholder();
        F_GETLK = placeholder();
        F_GETLK64 = placeholder();
        F_GETOWN = placeholder();
        F_OK = placeholder();
        F_RDLCK = placeholder();
        F_SETFD = placeholder();
        F_SETFL = placeholder();
        F_SETLK = placeholder();
        F_SETLK64 = placeholder();
        F_SETLKW = placeholder();
        F_SETLKW64 = placeholder();
        F_SETOWN = placeholder();
        F_UNLCK = placeholder();
        F_WRLCK = placeholder();
        IFA_F_DADFAILED = placeholder();
        IFA_F_DEPRECATED = placeholder();
        IFA_F_HOMEADDRESS = placeholder();
        IFA_F_NODAD = placeholder();
        IFA_F_OPTIMISTIC = placeholder();
        IFA_F_PERMANENT = placeholder();
        IFA_F_SECONDARY = placeholder();
        IFA_F_TEMPORARY = placeholder();
        IFA_F_TENTATIVE = placeholder();
        IFF_ALLMULTI = placeholder();
        IFF_AUTOMEDIA = placeholder();
        IFF_BROADCAST = placeholder();
        IFF_DEBUG = placeholder();
        IFF_DYNAMIC = placeholder();
        IFF_LOOPBACK = placeholder();
        IFF_MASTER = placeholder();
        IFF_MULTICAST = placeholder();
        IFF_NOARP = placeholder();
        IFF_NOTRAILERS = placeholder();
        IFF_POINTOPOINT = placeholder();
        IFF_PORTSEL = placeholder();
        IFF_PROMISC = placeholder();
        IFF_RUNNING = placeholder();
        IFF_SLAVE = placeholder();
        IFF_UP = placeholder();
        IPPROTO_ICMP = placeholder();
        IPPROTO_ICMPV6 = placeholder();
        IPPROTO_IP = placeholder();
        IPPROTO_IPV6 = placeholder();
        IPPROTO_RAW = placeholder();
        IPPROTO_TCP = placeholder();
        IPPROTO_UDP = placeholder();
        IPV6_CHECKSUM = placeholder();
        IPV6_MULTICAST_HOPS = placeholder();
        IPV6_MULTICAST_IF = placeholder();
        IPV6_MULTICAST_LOOP = placeholder();
        IPV6_RECVDSTOPTS = placeholder();
        IPV6_RECVHOPLIMIT = placeholder();
        IPV6_RECVHOPOPTS = placeholder();
        IPV6_RECVPKTINFO = placeholder();
        IPV6_RECVRTHDR = placeholder();
        IPV6_RECVTCLASS = placeholder();
        IPV6_TCLASS = placeholder();
        IPV6_UNICAST_HOPS = placeholder();
        IPV6_V6ONLY = placeholder();
        IP_MULTICAST_IF = placeholder();
        IP_MULTICAST_LOOP = placeholder();
        IP_MULTICAST_TTL = placeholder();
        IP_TOS = placeholder();
        IP_TTL = placeholder();
        MAP_FIXED = placeholder();
        MAP_PRIVATE = placeholder();
        MAP_SHARED = placeholder();
        MCAST_JOIN_GROUP = placeholder();
        MCAST_LEAVE_GROUP = placeholder();
        MCAST_JOIN_SOURCE_GROUP = placeholder();
        MCAST_LEAVE_SOURCE_GROUP = placeholder();
        MCAST_BLOCK_SOURCE = placeholder();
        MCAST_UNBLOCK_SOURCE = placeholder();
        MCL_CURRENT = placeholder();
        MCL_FUTURE = placeholder();
        MSG_CTRUNC = placeholder();
        MSG_DONTROUTE = placeholder();
        MSG_EOR = placeholder();
        MSG_OOB = placeholder();
        MSG_PEEK = placeholder();
        MSG_TRUNC = placeholder();
        MSG_WAITALL = placeholder();
        MS_ASYNC = placeholder();
        MS_INVALIDATE = placeholder();
        MS_SYNC = placeholder();
        NI_DGRAM = placeholder();
        NI_NAMEREQD = placeholder();
        NI_NOFQDN = placeholder();
        NI_NUMERICHOST = placeholder();
        NI_NUMERICSERV = placeholder();
        O_ACCMODE = placeholder();
        O_APPEND = placeholder();
        O_CREAT = placeholder();
        O_EXCL = placeholder();
        O_NOCTTY = placeholder();
        O_NOFOLLOW = placeholder();
        O_NONBLOCK = placeholder();
        O_RDONLY = placeholder();
        O_RDWR = placeholder();
        O_SYNC = placeholder();
        O_TRUNC = placeholder();
        O_WRONLY = placeholder();
        POLLERR = placeholder();
        POLLHUP = placeholder();
        POLLIN = placeholder();
        POLLNVAL = placeholder();
        POLLOUT = placeholder();
        POLLPRI = placeholder();
        POLLRDBAND = placeholder();
        POLLRDNORM = placeholder();
        POLLWRBAND = placeholder();
        POLLWRNORM = placeholder();
        PR_GET_DUMPABLE = placeholder();
        PR_SET_DUMPABLE = placeholder();
        PR_SET_NO_NEW_PRIVS = placeholder();
        PROT_EXEC = placeholder();
        PROT_NONE = placeholder();
        PROT_READ = placeholder();
        PROT_WRITE = placeholder();
        R_OK = placeholder();
        RT_SCOPE_HOST = placeholder();
        RT_SCOPE_LINK = placeholder();
        RT_SCOPE_NOWHERE = placeholder();
        RT_SCOPE_SITE = placeholder();
        RT_SCOPE_UNIVERSE = placeholder();
        SEEK_CUR = placeholder();
        SEEK_END = placeholder();
        SEEK_SET = placeholder();
        SHUT_RD = placeholder();
        SHUT_RDWR = placeholder();
        SHUT_WR = placeholder();
        SIGABRT = placeholder();
        SIGALRM = placeholder();
        SIGBUS = placeholder();
        SIGCHLD = placeholder();
        SIGCONT = placeholder();
        SIGFPE = placeholder();
        SIGHUP = placeholder();
        SIGILL = placeholder();
        SIGINT = placeholder();
        SIGIO = placeholder();
        SIGKILL = placeholder();
        SIGPIPE = placeholder();
        SIGPROF = placeholder();
        SIGPWR = placeholder();
        SIGQUIT = placeholder();
        SIGRTMAX = placeholder();
        SIGRTMIN = placeholder();
        SIGSEGV = placeholder();
        SIGSTKFLT = placeholder();
        SIGSTOP = placeholder();
        SIGSYS = placeholder();
        SIGTERM = placeholder();
        SIGTRAP = placeholder();
        SIGTSTP = placeholder();
        SIGTTIN = placeholder();
        SIGTTOU = placeholder();
        SIGURG = placeholder();
        SIGUSR1 = placeholder();
        SIGUSR2 = placeholder();
        SIGVTALRM = placeholder();
        SIGWINCH = placeholder();
        SIGXCPU = placeholder();
        SIGXFSZ = placeholder();
        SIOCGIFADDR = placeholder();
        SIOCGIFBRDADDR = placeholder();
        SIOCGIFDSTADDR = placeholder();
        SIOCGIFNETMASK = placeholder();
        SOCK_DGRAM = placeholder();
        SOCK_RAW = placeholder();
        SOCK_SEQPACKET = placeholder();
        SOCK_STREAM = placeholder();
        SOL_SOCKET = placeholder();
        SO_BINDTODEVICE = placeholder();
        SO_BROADCAST = placeholder();
        SO_DEBUG = placeholder();
        SO_DONTROUTE = placeholder();
        SO_ERROR = placeholder();
        SO_KEEPALIVE = placeholder();
        SO_LINGER = placeholder();
        SO_OOBINLINE = placeholder();
        SO_PASSCRED = placeholder();
        SO_PEERCRED = placeholder();
        SO_RCVBUF = placeholder();
        SO_RCVLOWAT = placeholder();
        SO_RCVTIMEO = placeholder();
        SO_REUSEADDR = placeholder();
        SO_SNDBUF = placeholder();
        SO_SNDLOWAT = placeholder();
        SO_SNDTIMEO = placeholder();
        SO_TYPE = placeholder();
        STDERR_FILENO = placeholder();
        STDIN_FILENO = placeholder();
        STDOUT_FILENO = placeholder();
        S_IFBLK = placeholder();
        S_IFCHR = placeholder();
        S_IFDIR = placeholder();
        S_IFIFO = placeholder();
        S_IFLNK = placeholder();
        S_IFMT = placeholder();
        S_IFREG = placeholder();
        S_IFSOCK = placeholder();
        S_IRGRP = placeholder();
        S_IROTH = placeholder();
        S_IRUSR = placeholder();
        S_IRWXG = placeholder();
        S_IRWXO = placeholder();
        S_IRWXU = placeholder();
        S_ISGID = placeholder();
        S_ISUID = placeholder();
        S_ISVTX = placeholder();
        S_IWGRP = placeholder();
        S_IWOTH = placeholder();
        S_IWUSR = placeholder();
        S_IXGRP = placeholder();
        S_IXOTH = placeholder();
        S_IXUSR = placeholder();
        TCP_NODELAY = placeholder();
        WCONTINUED = placeholder();
        WEXITED = placeholder();
        WNOHANG = placeholder();
        WNOWAIT = placeholder();
        WSTOPPED = placeholder();
        WUNTRACED = placeholder();
        W_OK = placeholder();
        X_OK = placeholder();
        _SC_2_CHAR_TERM = placeholder();
        _SC_2_C_BIND = placeholder();
        _SC_2_C_DEV = placeholder();
        _SC_2_C_VERSION = placeholder();
        _SC_2_FORT_DEV = placeholder();
        _SC_2_FORT_RUN = placeholder();
        _SC_2_LOCALEDEF = placeholder();
        _SC_2_SW_DEV = placeholder();
        _SC_2_UPE = placeholder();
        _SC_2_VERSION = placeholder();
        _SC_AIO_LISTIO_MAX = placeholder();
        _SC_AIO_MAX = placeholder();
        _SC_AIO_PRIO_DELTA_MAX = placeholder();
        _SC_ARG_MAX = placeholder();
        _SC_ASYNCHRONOUS_IO = placeholder();
        _SC_ATEXIT_MAX = placeholder();
        _SC_AVPHYS_PAGES = placeholder();
        _SC_BC_BASE_MAX = placeholder();
        _SC_BC_DIM_MAX = placeholder();
        _SC_BC_SCALE_MAX = placeholder();
        _SC_BC_STRING_MAX = placeholder();
        _SC_CHILD_MAX = placeholder();
        _SC_CLK_TCK = placeholder();
        _SC_COLL_WEIGHTS_MAX = placeholder();
        _SC_DELAYTIMER_MAX = placeholder();
        _SC_EXPR_NEST_MAX = placeholder();
        _SC_FSYNC = placeholder();
        _SC_GETGR_R_SIZE_MAX = placeholder();
        _SC_GETPW_R_SIZE_MAX = placeholder();
        _SC_IOV_MAX = placeholder();
        _SC_JOB_CONTROL = placeholder();
        _SC_LINE_MAX = placeholder();
        _SC_LOGIN_NAME_MAX = placeholder();
        _SC_MAPPED_FILES = placeholder();
        _SC_MEMLOCK = placeholder();
        _SC_MEMLOCK_RANGE = placeholder();
        _SC_MEMORY_PROTECTION = placeholder();
        _SC_MESSAGE_PASSING = placeholder();
        _SC_MQ_OPEN_MAX = placeholder();
        _SC_MQ_PRIO_MAX = placeholder();
        _SC_NGROUPS_MAX = placeholder();
        _SC_NPROCESSORS_CONF = placeholder();
        _SC_NPROCESSORS_ONLN = placeholder();
        _SC_OPEN_MAX = placeholder();
        _SC_PAGESIZE = placeholder();
        _SC_PAGE_SIZE = placeholder();
        _SC_PASS_MAX = placeholder();
        _SC_PHYS_PAGES = placeholder();
        _SC_PRIORITIZED_IO = placeholder();
        _SC_PRIORITY_SCHEDULING = placeholder();
        _SC_REALTIME_SIGNALS = placeholder();
        _SC_RE_DUP_MAX = placeholder();
        _SC_RTSIG_MAX = placeholder();
        _SC_SAVED_IDS = placeholder();
        _SC_SEMAPHORES = placeholder();
        _SC_SEM_NSEMS_MAX = placeholder();
        _SC_SEM_VALUE_MAX = placeholder();
        _SC_SHARED_MEMORY_OBJECTS = placeholder();
        _SC_SIGQUEUE_MAX = placeholder();
        _SC_STREAM_MAX = placeholder();
        _SC_SYNCHRONIZED_IO = placeholder();
        _SC_THREADS = placeholder();
        _SC_THREAD_ATTR_STACKADDR = placeholder();
        _SC_THREAD_ATTR_STACKSIZE = placeholder();
        _SC_THREAD_DESTRUCTOR_ITERATIONS = placeholder();
        _SC_THREAD_KEYS_MAX = placeholder();
        _SC_THREAD_PRIORITY_SCHEDULING = placeholder();
        _SC_THREAD_PRIO_INHERIT = placeholder();
        _SC_THREAD_PRIO_PROTECT = placeholder();
        _SC_THREAD_SAFE_FUNCTIONS = placeholder();
        _SC_THREAD_STACK_MIN = placeholder();
        _SC_THREAD_THREADS_MAX = placeholder();
        _SC_TIMERS = placeholder();
        _SC_TIMER_MAX = placeholder();
        _SC_TTY_NAME_MAX = placeholder();
        _SC_TZNAME_MAX = placeholder();
        _SC_VERSION = placeholder();
        _SC_XBS5_ILP32_OFF32 = placeholder();
        _SC_XBS5_ILP32_OFFBIG = placeholder();
        _SC_XBS5_LP64_OFF64 = placeholder();
        _SC_XBS5_LPBIG_OFFBIG = placeholder();
        _SC_XOPEN_CRYPT = placeholder();
        _SC_XOPEN_ENH_I18N = placeholder();
        _SC_XOPEN_LEGACY = placeholder();
        _SC_XOPEN_REALTIME = placeholder();
        _SC_XOPEN_REALTIME_THREADS = placeholder();
        _SC_XOPEN_SHM = placeholder();
        _SC_XOPEN_UNIX = placeholder();
        _SC_XOPEN_VERSION = placeholder();
        _SC_XOPEN_XCU_VERSION = placeholder();
        initConstants();
    }

    public static String gaiName(int error) {
        if (error == EAI_AGAIN) {
            return "EAI_AGAIN";
        }
        if (error == EAI_BADFLAGS) {
            return "EAI_BADFLAGS";
        }
        if (error == EAI_FAIL) {
            return "EAI_FAIL";
        }
        if (error == EAI_FAMILY) {
            return "EAI_FAMILY";
        }
        if (error == EAI_MEMORY) {
            return "EAI_MEMORY";
        }
        if (error == EAI_NODATA) {
            return "EAI_NODATA";
        }
        if (error == EAI_NONAME) {
            return "EAI_NONAME";
        }
        if (error == EAI_OVERFLOW) {
            return "EAI_OVERFLOW";
        }
        if (error == EAI_SERVICE) {
            return "EAI_SERVICE";
        }
        if (error == EAI_SOCKTYPE) {
            return "EAI_SOCKTYPE";
        }
        if (error == EAI_SYSTEM) {
            return "EAI_SYSTEM";
        }
        return null;
    }

    public static String errnoName(int errno) {
        if (errno == E2BIG) {
            return "E2BIG";
        }
        if (errno == EACCES) {
            return "EACCES";
        }
        if (errno == EADDRINUSE) {
            return "EADDRINUSE";
        }
        if (errno == EADDRNOTAVAIL) {
            return "EADDRNOTAVAIL";
        }
        if (errno == EAFNOSUPPORT) {
            return "EAFNOSUPPORT";
        }
        if (errno == EAGAIN) {
            return "EAGAIN";
        }
        if (errno == EALREADY) {
            return "EALREADY";
        }
        if (errno == EBADF) {
            return "EBADF";
        }
        if (errno == EBADMSG) {
            return "EBADMSG";
        }
        if (errno == EBUSY) {
            return "EBUSY";
        }
        if (errno == ECANCELED) {
            return "ECANCELED";
        }
        if (errno == ECHILD) {
            return "ECHILD";
        }
        if (errno == ECONNABORTED) {
            return "ECONNABORTED";
        }
        if (errno == ECONNREFUSED) {
            return "ECONNREFUSED";
        }
        if (errno == ECONNRESET) {
            return "ECONNRESET";
        }
        if (errno == EDEADLK) {
            return "EDEADLK";
        }
        if (errno == EDESTADDRREQ) {
            return "EDESTADDRREQ";
        }
        if (errno == EDOM) {
            return "EDOM";
        }
        if (errno == EDQUOT) {
            return "EDQUOT";
        }
        if (errno == EEXIST) {
            return "EEXIST";
        }
        if (errno == EFAULT) {
            return "EFAULT";
        }
        if (errno == EFBIG) {
            return "EFBIG";
        }
        if (errno == EHOSTUNREACH) {
            return "EHOSTUNREACH";
        }
        if (errno == EIDRM) {
            return "EIDRM";
        }
        if (errno == EILSEQ) {
            return "EILSEQ";
        }
        if (errno == EINPROGRESS) {
            return "EINPROGRESS";
        }
        if (errno == EINTR) {
            return "EINTR";
        }
        if (errno == EINVAL) {
            return "EINVAL";
        }
        if (errno == EIO) {
            return "EIO";
        }
        if (errno == EISCONN) {
            return "EISCONN";
        }
        if (errno == EISDIR) {
            return "EISDIR";
        }
        if (errno == ELOOP) {
            return "ELOOP";
        }
        if (errno == EMFILE) {
            return "EMFILE";
        }
        if (errno == EMLINK) {
            return "EMLINK";
        }
        if (errno == EMSGSIZE) {
            return "EMSGSIZE";
        }
        if (errno == EMULTIHOP) {
            return "EMULTIHOP";
        }
        if (errno == ENAMETOOLONG) {
            return "ENAMETOOLONG";
        }
        if (errno == ENETDOWN) {
            return "ENETDOWN";
        }
        if (errno == ENETRESET) {
            return "ENETRESET";
        }
        if (errno == ENETUNREACH) {
            return "ENETUNREACH";
        }
        if (errno == ENFILE) {
            return "ENFILE";
        }
        if (errno == ENOBUFS) {
            return "ENOBUFS";
        }
        if (errno == ENODATA) {
            return "ENODATA";
        }
        if (errno == ENODEV) {
            return "ENODEV";
        }
        if (errno == ENOENT) {
            return "ENOENT";
        }
        if (errno == ENOEXEC) {
            return "ENOEXEC";
        }
        if (errno == ENOLCK) {
            return "ENOLCK";
        }
        if (errno == ENOLINK) {
            return "ENOLINK";
        }
        if (errno == ENOMEM) {
            return "ENOMEM";
        }
        if (errno == ENOMSG) {
            return "ENOMSG";
        }
        if (errno == ENOPROTOOPT) {
            return "ENOPROTOOPT";
        }
        if (errno == ENOSPC) {
            return "ENOSPC";
        }
        if (errno == ENOSR) {
            return "ENOSR";
        }
        if (errno == ENOSTR) {
            return "ENOSTR";
        }
        if (errno == ENOSYS) {
            return "ENOSYS";
        }
        if (errno == ENOTCONN) {
            return "ENOTCONN";
        }
        if (errno == ENOTDIR) {
            return "ENOTDIR";
        }
        if (errno == ENOTEMPTY) {
            return "ENOTEMPTY";
        }
        if (errno == ENOTSOCK) {
            return "ENOTSOCK";
        }
        if (errno == ENOTSUP) {
            return "ENOTSUP";
        }
        if (errno == ENOTTY) {
            return "ENOTTY";
        }
        if (errno == ENXIO) {
            return "ENXIO";
        }
        if (errno == EOPNOTSUPP) {
            return "EOPNOTSUPP";
        }
        if (errno == EOVERFLOW) {
            return "EOVERFLOW";
        }
        if (errno == EPERM) {
            return "EPERM";
        }
        if (errno == EPIPE) {
            return "EPIPE";
        }
        if (errno == EPROTO) {
            return "EPROTO";
        }
        if (errno == EPROTONOSUPPORT) {
            return "EPROTONOSUPPORT";
        }
        if (errno == EPROTOTYPE) {
            return "EPROTOTYPE";
        }
        if (errno == ERANGE) {
            return "ERANGE";
        }
        if (errno == EROFS) {
            return "EROFS";
        }
        if (errno == ESPIPE) {
            return "ESPIPE";
        }
        if (errno == ESRCH) {
            return "ESRCH";
        }
        if (errno == ESTALE) {
            return "ESTALE";
        }
        if (errno == ETIME) {
            return "ETIME";
        }
        if (errno == ETIMEDOUT) {
            return "ETIMEDOUT";
        }
        if (errno == ETXTBSY) {
            return "ETXTBSY";
        }
        if (errno == EXDEV) {
            return "EXDEV";
        }
        return null;
    }

    private static int placeholder() {
        return 0;
    }
}
