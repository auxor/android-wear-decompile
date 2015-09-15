package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collection;

public class ScanSettings implements Parcelable {
    public static final Creator<ScanSettings> CREATOR;
    public Collection<WifiChannel> channelSet;

    public ScanSettings(ScanSettings source) {
        if (source.channelSet != null) {
            this.channelSet = new ArrayList(source.channelSet);
        }
    }

    public boolean isValid() {
        for (WifiChannel channel : this.channelSet) {
            if (!channel.isValid()) {
                return false;
            }
        }
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.channelSet == null ? 0 : this.channelSet.size());
        if (this.channelSet != null) {
            for (WifiChannel channel : this.channelSet) {
                channel.writeToParcel(out, flags);
            }
        }
    }

    static {
        CREATOR = new Creator<ScanSettings>() {
            public android.net.wifi.ScanSettings createFromParcel(android.os.Parcel r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.wifi.ScanSettings.1.createFromParcel(android.os.Parcel):android.net.wifi.ScanSettings
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.wifi.ScanSettings.1.createFromParcel(android.os.Parcel):android.net.wifi.ScanSettings
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.ScanSettings.1.createFromParcel(android.os.Parcel):android.net.wifi.ScanSettings");
            }

            public ScanSettings[] newArray(int size) {
                return new ScanSettings[size];
            }
        };
    }
}
