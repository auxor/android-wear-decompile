package com.google.android.collect;

import com.android.internal.telephony.RILConstants;
import java.util.ArrayList;
import java.util.Collections;

public class Lists {
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList();
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList(((elements.length * RILConstants.RIL_REQUEST_SET_UNSOL_CELL_INFO_LIST_RATE) / 100) + 5);
        Collections.addAll(list, elements);
        return list;
    }
}
