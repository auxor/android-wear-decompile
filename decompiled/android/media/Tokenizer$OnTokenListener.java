package android.media;

/* compiled from: WebVttRenderer */
interface Tokenizer$OnTokenListener {
    void onData(String str);

    void onEnd(String str);

    void onLineEnd();

    void onStart(String str, String[] strArr, String str2);

    void onTimeStamp(long j);
}
