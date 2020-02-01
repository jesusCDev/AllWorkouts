package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;

import com.allvens.allworkouts.R;

public class TextDocumentation_OpenSource extends TextDocumentation_Manager{
    public TextDocumentation_OpenSource(Context context) {
        super(context);

        create_Title(R.string.open_source_title);
        create_Paragraph(R.string.open_source_date);

        create_Paragraph(R.string.open_source_summary);
        create_SubTitle(R.string.open_source_section_1_sub_title);
        create_Paragraph(R.string.open_source_section_1_summary);
    }
}
