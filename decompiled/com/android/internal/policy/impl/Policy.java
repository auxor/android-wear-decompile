package com.android.internal.policy.impl;

import android.content.Context;
import android.view.FallbackEventHandler;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManagerPolicy;
import com.android.internal.policy.IPolicy;

public class Policy implements IPolicy {
    private static final String TAG = "PhonePolicy";
    private static final String[] preload_classes = null;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.Policy.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.Policy.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.Policy.<clinit>():void");
    }

    public Window makeNewWindow(Context context) {
        return new PhoneWindow(context);
    }

    public LayoutInflater makeNewLayoutInflater(Context context) {
        return new PhoneLayoutInflater(context);
    }

    public WindowManagerPolicy makeNewWindowManager() {
        return new PhoneWindowManager();
    }

    public FallbackEventHandler makeNewFallbackEventHandler(Context context) {
        return new PhoneFallbackEventHandler(context);
    }
}
