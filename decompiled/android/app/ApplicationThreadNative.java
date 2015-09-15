package android.app;

import android.os.Binder;
import android.os.IBinder;

public abstract class ApplicationThreadNative extends Binder implements IApplicationThread {
    public boolean onTransact(int r114, android.os.Parcel r115, android.os.Parcel r116, int r117) throws android.os.RemoteException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:186:0x06f5
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.modifyBlocksTree(BlockProcessor.java:248)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r113 = this;
        switch(r114) {
            case 1: goto L_0x0008;
            case 2: goto L_0x0003;
            case 3: goto L_0x0039;
            case 4: goto L_0x005c;
            case 5: goto L_0x009b;
            case 6: goto L_0x00c7;
            case 7: goto L_0x00e4;
            case 8: goto L_0x01c5;
            case 9: goto L_0x01e2;
            case 10: goto L_0x0206;
            case 11: goto L_0x0258;
            case 12: goto L_0x0313;
            case 13: goto L_0x0328;
            case 14: goto L_0x03d4;
            case 15: goto L_0x0003;
            case 16: goto L_0x03ee;
            case 17: goto L_0x02da;
            case 18: goto L_0x0409;
            case 19: goto L_0x0450;
            case 20: goto L_0x0289;
            case 21: goto L_0x02bb;
            case 22: goto L_0x045d;
            case 23: goto L_0x04ad;
            case 24: goto L_0x0500;
            case 25: goto L_0x050d;
            case 26: goto L_0x0177;
            case 27: goto L_0x007b;
            case 28: goto L_0x0520;
            case 29: goto L_0x0559;
            case 30: goto L_0x056e;
            case 31: goto L_0x059b;
            case 32: goto L_0x07af;
            case 33: goto L_0x03e1;
            case 34: goto L_0x05c2;
            case 35: goto L_0x05dd;
            case 36: goto L_0x05f2;
            case 37: goto L_0x062b;
            case 38: goto L_0x0416;
            case 39: goto L_0x0423;
            case 40: goto L_0x0659;
            case 41: goto L_0x066e;
            case 42: goto L_0x068f;
            case 43: goto L_0x06a4;
            case 44: goto L_0x06f9;
            case 45: goto L_0x0485;
            case 46: goto L_0x0723;
            case 47: goto L_0x074d;
            case 48: goto L_0x0765;
            case 49: goto L_0x0789;
            case 50: goto L_0x07d4;
            case 51: goto L_0x07ec;
            case 52: goto L_0x080a;
            case 53: goto L_0x0828;
            case 54: goto L_0x0840;
            case 55: goto L_0x0865;
            default: goto L_0x0003;
        };
    L_0x0003:
        r5 = super.onTransact(r114, r115, r116, r117);
    L_0x0007:
        return r5;
    L_0x0008:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0033;
    L_0x0019:
        r7 = 1;
    L_0x001a:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0035;
    L_0x0020:
        r8 = 1;
    L_0x0021:
        r9 = r115.readInt();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0037;
    L_0x002b:
        r10 = 1;
    L_0x002c:
        r5 = r113;
        r5.schedulePauseActivity(r6, r7, r8, r9, r10);
        r5 = 1;
        goto L_0x0007;
    L_0x0033:
        r7 = 0;
        goto L_0x001a;
    L_0x0035:
        r8 = 0;
        goto L_0x0021;
    L_0x0037:
        r10 = 0;
        goto L_0x002c;
    L_0x0039:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0059;
    L_0x004a:
        r109 = 1;
    L_0x004c:
        r9 = r115.readInt();
        r0 = r113;
        r1 = r109;
        r0.scheduleStopActivity(r6, r1, r9);
        r5 = 1;
        goto L_0x0007;
    L_0x0059:
        r109 = 0;
        goto L_0x004c;
    L_0x005c:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0078;
    L_0x006d:
        r109 = 1;
    L_0x006f:
        r0 = r113;
        r1 = r109;
        r0.scheduleWindowVisibility(r6, r1);
        r5 = 1;
        goto L_0x0007;
    L_0x0078:
        r109 = 0;
        goto L_0x006f;
    L_0x007b:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0098;
    L_0x008c:
        r110 = 1;
    L_0x008e:
        r0 = r113;
        r1 = r110;
        r0.scheduleSleeping(r6, r1);
        r5 = 1;
        goto L_0x0007;
    L_0x0098:
        r110 = 0;
        goto L_0x008e;
    L_0x009b:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r20 = r115.readInt();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x00c4;
    L_0x00b0:
        r26 = 1;
    L_0x00b2:
        r106 = r115.readBundle();
        r0 = r113;
        r1 = r20;
        r2 = r26;
        r3 = r106;
        r0.scheduleResumeActivity(r6, r1, r2, r3);
        r5 = 1;
        goto L_0x0007;
    L_0x00c4:
        r26 = 0;
        goto L_0x00b2;
    L_0x00c7:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = android.app.ResultInfo.CREATOR;
        r0 = r115;
        r23 = r0.createTypedArrayList(r5);
        r0 = r113;
        r1 = r23;
        r0.scheduleSendResult(r6, r1);
        r5 = 1;
        goto L_0x0007;
    L_0x00e4:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = android.content.Intent.CREATOR;
        r0 = r115;
        r12 = r5.createFromParcel(r0);
        r12 = (android.content.Intent) r12;
        r6 = r115.readStrongBinder();
        r14 = r115.readInt();
        r5 = android.content.pm.ActivityInfo.CREATOR;
        r0 = r115;
        r15 = r5.createFromParcel(r0);
        r15 = (android.content.pm.ActivityInfo) r15;
        r5 = android.content.res.Configuration.CREATOR;
        r0 = r115;
        r16 = r5.createFromParcel(r0);
        r16 = (android.content.res.Configuration) r16;
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r17 = r5.createFromParcel(r0);
        r17 = (android.content.res.CompatibilityInfo) r17;
        r18 = r115.readString();
        r5 = r115.readStrongBinder();
        r19 = com.android.internal.app.IVoiceInteractor.Stub.asInterface(r5);
        r20 = r115.readInt();
        r21 = r115.readBundle();
        r22 = r115.readPersistableBundle();
        r5 = android.app.ResultInfo.CREATOR;
        r0 = r115;
        r23 = r0.createTypedArrayList(r5);
        r5 = com.android.internal.content.ReferrerIntent.CREATOR;
        r0 = r115;
        r24 = r0.createTypedArrayList(r5);
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x016e;
    L_0x0149:
        r25 = 1;
    L_0x014b:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0171;
    L_0x0151:
        r26 = 1;
    L_0x0153:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0174;
    L_0x0159:
        r5 = android.app.ProfilerInfo.CREATOR;
        r0 = r115;
        r5 = r5.createFromParcel(r0);
        r5 = (android.app.ProfilerInfo) r5;
        r27 = r5;
    L_0x0165:
        r11 = r113;
        r13 = r6;
        r11.scheduleLaunchActivity(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);
        r5 = 1;
        goto L_0x0007;
    L_0x016e:
        r25 = 0;
        goto L_0x014b;
    L_0x0171:
        r26 = 0;
        goto L_0x0153;
    L_0x0174:
        r27 = 0;
        goto L_0x0165;
    L_0x0177:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = android.app.ResultInfo.CREATOR;
        r0 = r115;
        r23 = r0.createTypedArrayList(r5);
        r5 = com.android.internal.content.ReferrerIntent.CREATOR;
        r0 = r115;
        r24 = r0.createTypedArrayList(r5);
        r9 = r115.readInt();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x01c2;
    L_0x019c:
        r25 = 1;
    L_0x019e:
        r34 = 0;
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x01b0;
    L_0x01a6:
        r5 = android.content.res.Configuration.CREATOR;
        r0 = r115;
        r34 = r5.createFromParcel(r0);
        r34 = (android.content.res.Configuration) r34;
    L_0x01b0:
        r28 = r113;
        r29 = r6;
        r30 = r23;
        r31 = r24;
        r32 = r9;
        r33 = r25;
        r28.scheduleRelaunchActivity(r29, r30, r31, r32, r33, r34);
        r5 = 1;
        goto L_0x0007;
    L_0x01c2:
        r25 = 0;
        goto L_0x019e;
    L_0x01c5:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = com.android.internal.content.ReferrerIntent.CREATOR;
        r0 = r115;
        r24 = r0.createTypedArrayList(r5);
        r6 = r115.readStrongBinder();
        r0 = r113;
        r1 = r24;
        r0.scheduleNewIntent(r1, r6);
        r5 = 1;
        goto L_0x0007;
    L_0x01e2:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0203;
    L_0x01f3:
        r87 = 1;
    L_0x01f5:
        r9 = r115.readInt();
        r0 = r113;
        r1 = r87;
        r0.scheduleDestroyActivity(r6, r1, r9);
        r5 = 1;
        goto L_0x0007;
    L_0x0203:
        r87 = 0;
        goto L_0x01f5;
    L_0x0206:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = android.content.Intent.CREATOR;
        r0 = r115;
        r12 = r5.createFromParcel(r0);
        r12 = (android.content.Intent) r12;
        r5 = android.content.pm.ActivityInfo.CREATOR;
        r0 = r115;
        r15 = r5.createFromParcel(r0);
        r15 = (android.content.pm.ActivityInfo) r15;
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r17 = r5.createFromParcel(r0);
        r17 = (android.content.res.CompatibilityInfo) r17;
        r39 = r115.readInt();
        r40 = r115.readString();
        r41 = r115.readBundle();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0255;
    L_0x023d:
        r42 = 1;
    L_0x023f:
        r43 = r115.readInt();
        r44 = r115.readInt();
        r35 = r113;
        r36 = r12;
        r37 = r15;
        r38 = r17;
        r35.scheduleReceiver(r36, r37, r38, r39, r40, r41, r42, r43, r44);
        r5 = 1;
        goto L_0x0007;
    L_0x0255:
        r42 = 0;
        goto L_0x023f;
    L_0x0258:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r5 = android.content.pm.ServiceInfo.CREATOR;
        r0 = r115;
        r15 = r5.createFromParcel(r0);
        r15 = (android.content.pm.ServiceInfo) r15;
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r17 = r5.createFromParcel(r0);
        r17 = (android.content.res.CompatibilityInfo) r17;
        r44 = r115.readInt();
        r0 = r113;
        r1 = r29;
        r2 = r17;
        r3 = r44;
        r0.scheduleCreateService(r1, r15, r2, r3);
        r5 = 1;
        goto L_0x0007;
    L_0x0289:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r5 = android.content.Intent.CREATOR;
        r0 = r115;
        r12 = r5.createFromParcel(r0);
        r12 = (android.content.Intent) r12;
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x02b8;
    L_0x02a4:
        r103 = 1;
    L_0x02a6:
        r44 = r115.readInt();
        r0 = r113;
        r1 = r29;
        r2 = r103;
        r3 = r44;
        r0.scheduleBindService(r1, r12, r2, r3);
        r5 = 1;
        goto L_0x0007;
    L_0x02b8:
        r103 = 0;
        goto L_0x02a6;
    L_0x02bb:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r5 = android.content.Intent.CREATOR;
        r0 = r115;
        r12 = r5.createFromParcel(r0);
        r12 = (android.content.Intent) r12;
        r0 = r113;
        r1 = r29;
        r0.scheduleUnbindService(r1, r12);
        r5 = 1;
        goto L_0x0007;
    L_0x02da:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x030d;
    L_0x02eb:
        r30 = 1;
    L_0x02ed:
        r31 = r115.readInt();
        r32 = r115.readInt();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0310;
    L_0x02fb:
        r5 = android.content.Intent.CREATOR;
        r0 = r115;
        r33 = r5.createFromParcel(r0);
        r33 = (android.content.Intent) r33;
    L_0x0305:
        r28 = r113;
        r28.scheduleServiceArgs(r29, r30, r31, r32, r33);
        r5 = 1;
        goto L_0x0007;
    L_0x030d:
        r30 = 0;
        goto L_0x02ed;
    L_0x0310:
        r33 = 0;
        goto L_0x0305;
    L_0x0313:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r0 = r113;
        r1 = r29;
        r0.scheduleStopService(r1);
        r5 = 1;
        goto L_0x0007;
    L_0x0328:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r46 = r115.readString();
        r5 = android.content.pm.ApplicationInfo.CREATOR;
        r0 = r115;
        r15 = r5.createFromParcel(r0);
        r15 = (android.content.pm.ApplicationInfo) r15;
        r5 = android.content.pm.ProviderInfo.CREATOR;
        r0 = r115;
        r48 = r0.createTypedArrayList(r5);
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x03c5;
    L_0x034b:
        r49 = new android.content.ComponentName;
        r0 = r49;
        r1 = r115;
        r0.<init>(r1);
    L_0x0354:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x03c8;
    L_0x035a:
        r5 = android.app.ProfilerInfo.CREATOR;
        r0 = r115;
        r5 = r5.createFromParcel(r0);
        r5 = (android.app.ProfilerInfo) r5;
        r27 = r5;
    L_0x0366:
        r51 = r115.readBundle();
        r81 = r115.readStrongBinder();
        r52 = android.app.IInstrumentationWatcher.Stub.asInterface(r81);
        r81 = r115.readStrongBinder();
        r53 = android.app.IUiAutomationConnection.Stub.asInterface(r81);
        r54 = r115.readInt();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x03cb;
    L_0x0384:
        r55 = 1;
    L_0x0386:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x03ce;
    L_0x038c:
        r56 = 1;
    L_0x038e:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x03d1;
    L_0x0394:
        r57 = 1;
    L_0x0396:
        r5 = android.content.res.Configuration.CREATOR;
        r0 = r115;
        r34 = r5.createFromParcel(r0);
        r34 = (android.content.res.Configuration) r34;
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r17 = r5.createFromParcel(r0);
        r17 = (android.content.res.CompatibilityInfo) r17;
        r5 = 0;
        r0 = r115;
        r60 = r0.readHashMap(r5);
        r61 = r115.readBundle();
        r45 = r113;
        r47 = r15;
        r50 = r27;
        r58 = r34;
        r59 = r17;
        r45.bindApplication(r46, r47, r48, r49, r50, r51, r52, r53, r54, r55, r56, r57, r58, r59, r60, r61);
        r5 = 1;
        goto L_0x0007;
    L_0x03c5:
        r49 = 0;
        goto L_0x0354;
    L_0x03c8:
        r27 = 0;
        goto L_0x0366;
    L_0x03cb:
        r55 = 0;
        goto L_0x0386;
    L_0x03ce:
        r56 = 0;
        goto L_0x038e;
    L_0x03d1:
        r57 = 0;
        goto L_0x0396;
    L_0x03d4:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r113.scheduleExit();
        r5 = 1;
        goto L_0x0007;
    L_0x03e1:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r113.scheduleSuicide();
        r5 = 1;
        goto L_0x0007;
    L_0x03ee:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = android.content.res.Configuration.CREATOR;
        r0 = r115;
        r34 = r5.createFromParcel(r0);
        r34 = (android.content.res.Configuration) r34;
        r0 = r113;
        r1 = r34;
        r0.scheduleConfigurationChanged(r1);
        r5 = 1;
        goto L_0x0007;
    L_0x0409:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r113.updateTimeZone();
        r5 = 1;
        goto L_0x0007;
    L_0x0416:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r113.clearDnsCache();
        r5 = 1;
        goto L_0x0007;
    L_0x0423:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r102 = r115.readString();
        r98 = r115.readString();
        r85 = r115.readString();
        r5 = android.net.Uri.CREATOR;
        r0 = r115;
        r94 = r5.createFromParcel(r0);
        r94 = (android.net.Uri) r94;
        r0 = r113;
        r1 = r102;
        r2 = r98;
        r3 = r85;
        r4 = r94;
        r0.setHttpProxy(r1, r2, r3, r4);
        r5 = 1;
        goto L_0x0007;
    L_0x0450:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r113.processInBackground();
        r5 = 1;
        goto L_0x0007;
    L_0x045d:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r86 = r115.readFileDescriptor();
        r107 = r115.readStrongBinder();
        r33 = r115.readStringArray();
        if (r86 == 0) goto L_0x0482;
    L_0x0472:
        r5 = r86.getFileDescriptor();
        r0 = r113;
        r1 = r107;
        r2 = r33;
        r0.dumpService(r5, r1, r2);
        r86.close();	 Catch:{ IOException -> 0x087d }
    L_0x0482:
        r5 = 1;
        goto L_0x0007;
    L_0x0485:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r86 = r115.readFileDescriptor();
        r107 = r115.readStrongBinder();
        r33 = r115.readStringArray();
        if (r86 == 0) goto L_0x04aa;
    L_0x049a:
        r5 = r86.getFileDescriptor();
        r0 = r113;
        r1 = r107;
        r2 = r33;
        r0.dumpProvider(r5, r1, r2);
        r86.close();	 Catch:{ IOException -> 0x0880 }
    L_0x04aa:
        r5 = 1;
        goto L_0x0007;
    L_0x04ad:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = r115.readStrongBinder();
        r63 = android.content.IIntentReceiver.Stub.asInterface(r5);
        r5 = android.content.Intent.CREATOR;
        r0 = r115;
        r12 = r5.createFromParcel(r0);
        r12 = (android.content.Intent) r12;
        r39 = r115.readInt();
        r66 = r115.readString();
        r67 = r115.readBundle();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x04fa;
    L_0x04d8:
        r68 = 1;
    L_0x04da:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x04fd;
    L_0x04e0:
        r69 = 1;
    L_0x04e2:
        r43 = r115.readInt();
        r44 = r115.readInt();
        r62 = r113;
        r64 = r12;
        r65 = r39;
        r70 = r43;
        r71 = r44;
        r62.scheduleRegisteredReceiver(r63, r64, r65, r66, r67, r68, r69, r70, r71);
        r5 = 1;
        goto L_0x0007;
    L_0x04fa:
        r68 = 0;
        goto L_0x04da;
    L_0x04fd:
        r69 = 0;
        goto L_0x04e2;
    L_0x0500:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r113.scheduleLowMemory();
        r5 = 1;
        goto L_0x0007;
    L_0x050d:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r6 = r115.readStrongBinder();
        r0 = r113;
        r0.scheduleActivityConfigurationChanged(r6);
        r5 = 1;
        goto L_0x0007;
    L_0x0520:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0553;
    L_0x052d:
        r111 = 1;
    L_0x052f:
        r100 = r115.readInt();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0556;
    L_0x0539:
        r5 = android.app.ProfilerInfo.CREATOR;
        r0 = r115;
        r5 = r5.createFromParcel(r0);
        r5 = (android.app.ProfilerInfo) r5;
        r27 = r5;
    L_0x0545:
        r0 = r113;
        r1 = r111;
        r2 = r27;
        r3 = r100;
        r0.profilerControl(r1, r2, r3);
        r5 = 1;
        goto L_0x0007;
    L_0x0553:
        r111 = 0;
        goto L_0x052f;
    L_0x0556:
        r27 = 0;
        goto L_0x0545;
    L_0x0559:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r88 = r115.readInt();
        r0 = r113;
        r1 = r88;
        r0.setSchedulingGroup(r1);
        r5 = 1;
        goto L_0x0007;
    L_0x056e:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = android.content.pm.ApplicationInfo.CREATOR;
        r0 = r115;
        r79 = r5.createFromParcel(r0);
        r79 = (android.content.pm.ApplicationInfo) r79;
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r17 = r5.createFromParcel(r0);
        r17 = (android.content.res.CompatibilityInfo) r17;
        r80 = r115.readInt();
        r0 = r113;
        r1 = r79;
        r2 = r17;
        r3 = r80;
        r0.scheduleCreateBackupAgent(r1, r2, r3);
        r5 = 1;
        goto L_0x0007;
    L_0x059b:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = android.content.pm.ApplicationInfo.CREATOR;
        r0 = r115;
        r79 = r5.createFromParcel(r0);
        r79 = (android.content.pm.ApplicationInfo) r79;
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r17 = r5.createFromParcel(r0);
        r17 = (android.content.res.CompatibilityInfo) r17;
        r0 = r113;
        r1 = r79;
        r2 = r17;
        r0.scheduleDestroyBackupAgent(r1, r2);
        r5 = 1;
        goto L_0x0007;
    L_0x05c2:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r82 = r115.readInt();
        r95 = r115.readStringArray();
        r0 = r113;
        r1 = r82;
        r2 = r95;
        r0.dispatchPackageBroadcast(r1, r2);
        r5 = 1;
        goto L_0x0007;
    L_0x05dd:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r92 = r115.readString();
        r0 = r113;
        r1 = r92;
        r0.scheduleCrash(r1);
        r5 = 1;
        goto L_0x0007;
    L_0x05f2:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0625;
    L_0x05ff:
        r91 = 1;
    L_0x0601:
        r96 = r115.readString();
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x0628;
    L_0x060b:
        r5 = android.os.ParcelFileDescriptor.CREATOR;
        r0 = r115;
        r5 = r5.createFromParcel(r0);
        r5 = (android.os.ParcelFileDescriptor) r5;
        r86 = r5;
    L_0x0617:
        r0 = r113;
        r1 = r91;
        r2 = r96;
        r3 = r86;
        r0.dumpHeap(r1, r2, r3);
        r5 = 1;
        goto L_0x0007;
    L_0x0625:
        r91 = 0;
        goto L_0x0601;
    L_0x0628:
        r86 = 0;
        goto L_0x0617;
    L_0x062b:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r86 = r115.readFileDescriptor();
        r77 = r115.readStrongBinder();
        r99 = r115.readString();
        r33 = r115.readStringArray();
        if (r86 == 0) goto L_0x0656;
    L_0x0644:
        r5 = r86.getFileDescriptor();
        r0 = r113;
        r1 = r77;
        r2 = r99;
        r3 = r33;
        r0.dumpActivity(r5, r1, r2, r3);
        r86.close();	 Catch:{ IOException -> 0x0883 }
    L_0x0656:
        r5 = 1;
        goto L_0x0007;
    L_0x0659:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r108 = r115.readBundle();
        r0 = r113;
        r1 = r108;
        r0.setCoreSettings(r1);
        r5 = 1;
        goto L_0x0007;
    L_0x066e:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r97 = r115.readString();
        r5 = android.content.res.CompatibilityInfo.CREATOR;
        r0 = r115;
        r83 = r5.createFromParcel(r0);
        r83 = (android.content.res.CompatibilityInfo) r83;
        r0 = r113;
        r1 = r97;
        r2 = r83;
        r0.updatePackageCompatibilityInfo(r1, r2);
        r5 = 1;
        goto L_0x0007;
    L_0x068f:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r90 = r115.readInt();
        r0 = r113;
        r1 = r90;
        r0.scheduleTrimMemory(r1);
        r5 = 1;
        goto L_0x0007;
    L_0x06a4:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r86 = r115.readFileDescriptor();
        r5 = android.os.Debug.MemoryInfo.CREATOR;
        r0 = r115;
        r72 = r5.createFromParcel(r0);
        r72 = (android.os.Debug.MemoryInfo) r72;
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x06eb;
    L_0x06bf:
        r73 = 1;
    L_0x06c1:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x06ee;
    L_0x06c7:
        r74 = 1;
    L_0x06c9:
        r5 = r115.readInt();
        if (r5 == 0) goto L_0x06f1;
    L_0x06cf:
        r75 = 1;
    L_0x06d1:
        r33 = r115.readStringArray();
        if (r86 == 0) goto L_0x06e5;
    L_0x06d7:
        r71 = r86.getFileDescriptor();	 Catch:{ all -> 0x06f4 }
        r70 = r113;	 Catch:{ all -> 0x06f4 }
        r76 = r33;	 Catch:{ all -> 0x06f4 }
        r70.dumpMemInfo(r71, r72, r73, r74, r75, r76);	 Catch:{ all -> 0x06f4 }
        r86.close();
    L_0x06e5:
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x06eb:
        r73 = 0;
        goto L_0x06c1;
    L_0x06ee:
        r74 = 0;
        goto L_0x06c9;
    L_0x06f1:
        r75 = 0;
        goto L_0x06d1;
    L_0x06f4:
        r5 = move-exception;
        r86.close();	 Catch:{ IOException -> 0x0889 }
    L_0x06f8:
        throw r5;
    L_0x06f9:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r86 = r115.readFileDescriptor();
        r33 = r115.readStringArray();
        if (r86 == 0) goto L_0x0718;
    L_0x070a:
        r5 = r86.getFileDescriptor();	 Catch:{ all -> 0x071e }
        r0 = r113;	 Catch:{ all -> 0x071e }
        r1 = r33;	 Catch:{ all -> 0x071e }
        r0.dumpGfxInfo(r5, r1);	 Catch:{ all -> 0x071e }
        r86.close();
    L_0x0718:
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x071e:
        r5 = move-exception;
        r86.close();	 Catch:{ IOException -> 0x088f }
    L_0x0722:
        throw r5;
    L_0x0723:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r86 = r115.readFileDescriptor();
        r33 = r115.readStringArray();
        if (r86 == 0) goto L_0x0742;
    L_0x0734:
        r5 = r86.getFileDescriptor();	 Catch:{ all -> 0x0748 }
        r0 = r113;	 Catch:{ all -> 0x0748 }
        r1 = r33;	 Catch:{ all -> 0x0748 }
        r0.dumpDbInfo(r5, r1);	 Catch:{ all -> 0x0748 }
        r86.close();
    L_0x0742:
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x0748:
        r5 = move-exception;
        r86.close();	 Catch:{ IOException -> 0x0895 }
    L_0x074c:
        throw r5;
    L_0x074d:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r101 = r115.readStrongBinder();
        r0 = r113;
        r1 = r101;
        r0.unstableProviderDied(r1);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x0765:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r78 = r115.readStrongBinder();
        r104 = r115.readStrongBinder();
        r105 = r115.readInt();
        r0 = r113;
        r1 = r78;
        r2 = r104;
        r3 = r105;
        r0.requestAssistContextExtras(r1, r2, r3);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x0789:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r5 = r115.readInt();
        r11 = 1;
        if (r5 != r11) goto L_0x07ac;
    L_0x079b:
        r112 = 1;
    L_0x079d:
        r0 = r113;
        r1 = r29;
        r2 = r112;
        r0.scheduleTranslucentConversionComplete(r1, r2);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x07ac:
        r112 = 0;
        goto L_0x079d;
    L_0x07af:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r93 = new android.app.ActivityOptions;
        r5 = r115.readBundle();
        r0 = r93;
        r0.<init>(r5);
        r0 = r113;
        r1 = r29;
        r2 = r93;
        r0.scheduleOnNewActivityOptions(r1, r2);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x07d4:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r21 = r115.readInt();
        r0 = r113;
        r1 = r21;
        r0.setProcessState(r1);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x07ec:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r5 = android.content.pm.ProviderInfo.CREATOR;
        r0 = r115;
        r101 = r5.createFromParcel(r0);
        r101 = (android.content.pm.ProviderInfo) r101;
        r0 = r113;
        r1 = r101;
        r0.scheduleInstallProvider(r1);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x080a:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r89 = r115.readByte();
        r5 = 1;
        r0 = r89;
        if (r0 != r5) goto L_0x0826;
    L_0x081a:
        r5 = 1;
    L_0x081b:
        r0 = r113;
        r0.updateTimePrefs(r5);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x0826:
        r5 = 0;
        goto L_0x081b;
    L_0x0828:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r0 = r113;
        r1 = r29;
        r0.scheduleCancelVisibleBehind(r1);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x0840:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r5 = r115.readInt();
        if (r5 <= 0) goto L_0x0862;
    L_0x0851:
        r84 = 1;
    L_0x0853:
        r0 = r113;
        r1 = r29;
        r2 = r84;
        r0.scheduleBackgroundVisibleBehindChanged(r1, r2);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x0862:
        r84 = 0;
        goto L_0x0853;
    L_0x0865:
        r5 = "android.app.IApplicationThread";
        r0 = r115;
        r0.enforceInterface(r5);
        r29 = r115.readStrongBinder();
        r0 = r113;
        r1 = r29;
        r0.scheduleEnterAnimationComplete(r1);
        r116.writeNoException();
        r5 = 1;
        goto L_0x0007;
    L_0x087d:
        r5 = move-exception;
        goto L_0x0482;
    L_0x0880:
        r5 = move-exception;
        goto L_0x04aa;
    L_0x0883:
        r5 = move-exception;
        goto L_0x0656;
    L_0x0886:
        r5 = move-exception;
        goto L_0x06e5;
    L_0x0889:
        r11 = move-exception;
        goto L_0x06f8;
    L_0x088c:
        r5 = move-exception;
        goto L_0x0718;
    L_0x088f:
        r11 = move-exception;
        goto L_0x0722;
    L_0x0892:
        r5 = move-exception;
        goto L_0x0742;
    L_0x0895:
        r11 = move-exception;
        goto L_0x074c;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.ApplicationThreadNative.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
    }

    public static IApplicationThread asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IApplicationThread in = (IApplicationThread) obj.queryLocalInterface(IApplicationThread.descriptor);
        return in == null ? new ApplicationThreadProxy(obj) : in;
    }

    public ApplicationThreadNative() {
        attachInterface(this, IApplicationThread.descriptor);
    }

    public IBinder asBinder() {
        return this;
    }
}
