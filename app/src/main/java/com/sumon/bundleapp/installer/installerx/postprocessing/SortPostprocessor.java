package com.sumon.bundleapp.installer.installerx.postprocessing;

import com.sumon.bundleapp.installer.installerx.common.MutableSplitCategory;
import com.sumon.bundleapp.installer.installerx.common.MutableSplitPart;
import com.sumon.bundleapp.installer.installerx.common.ParserContext;

import java.util.Collections;
import java.util.Comparator;

public class SortPostprocessor implements Postprocessor {

    @Override
    public void process(ParserContext parserContext) {

        parserContext.getCategoriesList().sort(Comparator.comparingInt(o -> o.category().ordinal()));

        for (MutableSplitCategory category : parserContext.getCategoriesList()) {
            category.getPartsList().sort(Comparator.comparing(MutableSplitPart::name));
        }
    }

}
