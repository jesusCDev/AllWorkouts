package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;

import com.allvens.allworkouts.R;

public class TextDocumentation_PrivacyPolicy extends TextDocumentation_Manager{
    public TextDocumentation_PrivacyPolicy(Context context) {
        super(context);

        create_Title(R.string.privacy_policy_title);
        create_Paragraph(R.string.privacy_policy_date);

        create_Paragraph(R.string.privacy_policy_summary_data_collection);
        create_Paragraph(R.string.privacy_policy_summary_privacy_policy);
        create_Paragraph(R.string.privacy_policy_summary_end_user_license);

        OrderList olContainer_Pos = create_OrderList();

        OrderList olContainer_Alpha = create_OrderList();
        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.privacy_policy_section_1_sub_title));
        create_Paragraph(R.string.privacy_policy_section_1_body);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_1));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_1);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_2));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_2);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_3));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_3);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_4));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_4);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_5));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_5);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_6));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_6);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_7));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_7);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_8));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_8);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_9));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_9);
        create_SubTitle(olContainer_Alpha.get_NextAlpa() + getTextFromR(R.string.privacy_policy_section_1_body_ol_alpha_sub_title_10));
        create_Paragraph(R.string.privacy_policy_section_1_body_ol_alpha_body_10);

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.privacy_policy_section_2_sub_title));
        create_Paragraph(R.string.privacy_policy_section_2_body);

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.privacy_policy_section_3_sub_title));
        create_Paragraph(R.string.privacy_policy_section_3_body);

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.privacy_policy_section_4_sub_title));
        create_Paragraph(R.string.privacy_policy_section_4_body);

        create_SubTitle(olContainer_Pos.get_NextPos() + getTextFromR(R.string.privacy_policy_section_5_sub_title));
        create_Paragraph(R.string.privacy_policy_section_5_body);
    }
}
