package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;

import com.allvens.allworkouts.R;

public class TextDocumentation_TermsOfService extends TextDocumentation_Manager{

    public TextDocumentation_TermsOfService(Context context) {
        super(context);

        create_Title(R.string.terms_of_use_title);
        create_Paragraph(R.string.terms_of_use_date);

        create_Paragraph(R.string.terms_of_use_summary_1);
        create_Paragraph(R.string.terms_of_use_summary_2);

        OrderList olContainer_Pos = create_OrderList();

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.terms_of_use_section_2_sub_title));
        create_Paragraph(R.string.terms_of_use_section_2_body);

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.terms_of_use_section_3_sub_title));
        create_Paragraph(R.string.terms_of_use_section_3_body);

        OrderList olRelease_Alpha = create_OrderList();
        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.terms_of_use_section_4_sub_title));
        create_Paragraph(olRelease_Alpha.get_NextAlpa() + getTextFromR(R.string.terms_of_use_section_4_body_1));
        create_Paragraph(olRelease_Alpha.get_NextAlpa() + getTextFromR(R.string.terms_of_use_section_4_body_2));

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.terms_of_use_section_5_sub_title));
        create_Paragraph(R.string.terms_of_use_section_5_body);
    }
}
