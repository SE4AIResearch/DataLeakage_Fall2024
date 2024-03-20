package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import org.jetbrains.annotations.NotNull;

public interface OverlapLeakageData  {
    String getTrainModel();

    String getTrain();

    String getInvo();

    String getTrainMeth();

    String getCtx();



    boolean equals(@NotNull OverlapLeakageData o);
}
