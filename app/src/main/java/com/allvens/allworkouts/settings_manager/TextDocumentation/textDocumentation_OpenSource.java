package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;

import com.allvens.allworkouts.R;

public class textDocumentation_OpenSource extends textDocumentationManager {
    public textDocumentation_OpenSource(Context context) {
        super(context);

        createTitle(R.string.open_source_title);
        createParagraph(R.string.open_source_date);

        createParagraph(R.string.open_source_summary);
        createSubTitle(R.string.open_source_section_1_sub_title);
        createParagraph(R.string.open_source_section_1_summary);
    }
}
