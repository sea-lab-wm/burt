import altair as alt
import pandas
import openpyxl


def generate_json_for_each_question(question_dict, likert_scale_frequency):
    #  likert_scale_frequency = ['Always', 'Often', 'Sometimes', 'Rarely', 'Never]
    results = []
    answer_list = []
    question_name = ""
    for key, value in question_dict.items():
        question_name = key
        answer_list = value
    total_number_of_answer = len(answer_list)
    percentage_end = 0
    for i in range(4, -1, -1):
        count = answer_list.count(likert_scale_frequency[i])
        percentage = count / total_number_of_answer * 100
        if percentage != 0.0:
            result = {"question": question_name, "type": likert_scale_frequency[i], "value": count,
                      "percentage": percentage, "percentage_start": percentage_end,
                      "percentage_end": (percentage_end + percentage),
                      "position": (percentage_end + percentage / 2),
                      "numbers": "{:.1%}".format(count / total_number_of_answer)
                      }
            percentage_end = result["percentage_end"]

            results.append(result)

    # -------------------------------------------------------------
    # # first, compute percentage for "Sometimes"
    # result_middle = {"question": question_name, "type": likert_scale_frequency[2]}
    # count = answer_list.count(likert_scale_frequency[2])
    # result_middle["value"] = count
    # percentage_sometimes = count / total_number_of_answer
    # result_middle["percentage"] = percentage_sometimes * 100
    # result_middle["percentage_start"] = -percentage_sometimes / 2 * 100
    # result_middle["percentage_end"] = percentage_sometimes / 2 * 100
    # percentage_start = -percentage_sometimes / 2 * 100
    # percentage_start_2 = percentage_sometimes / 2 * 100
    # result_middle["position"] = 0
    # result_middle["numbers"] = "{:.2%}".format(count / total_number_of_answer)
    #
    # # second, compute percentage for 'Rarely', 'Never'
    # for i in range(3, 5):
    #     count = answer_list.count(likert_scale_frequency[i])
    #     percentage = count / total_number_of_answer * 100
    #     if percentage != 0.0:
    #         result = {"question": question_name, "type": likert_scale_frequency[i], "value": count,
    #                   "percentage": percentage, "percentage_start": (percentage_start - percentage),
    #                   "percentage_end": percentage_start, "position": (-percentage / 2 + percentage_start),
    #                   "numbers": "{:.2%}".format(count / total_number_of_answer)
    #                   }
    #
    #         percentage_start = result["percentage_start"]
    #         results.append(result)
    # results.append(result_middle)
    # # third, compute percentage for 'Always', 'Often'
    # for j in range(1, -1, -1):
    #     count = answer_list.count(likert_scale_frequency[j])
    #     percentage = count / total_number_of_answer * 100
    #     if percentage != 0.0:
    #         result = {"question": question_name, "type": likert_scale_frequency[j], "value": count,
    #                   "percentage": percentage, "percentage_start": percentage_start_2,
    #                   "percentage_end": (percentage_start_2 + percentage),
    #                   "position": (percentage_start_2 + percentage / 2),
    #                   "numbers": "{:.2%}".format(count / total_number_of_answer)
    #                   }
    #
    #         percentage_start_2 = result["percentage_end"]
    #         results.append(result)
    return results


def generate_chart_frequency(question_list, likert_scale_frequency):
    source_data_list = []
    for question in question_list:
        source_data_list.extend(generate_json_for_each_question(question, likert_scale_frequency))
    return source_data_list


if __name__ == '__main__':
    df = pandas.read_excel('../BURT ICSE’22 Evaluation Survey_August 23_latest.xlsx')

    screen_suggestion_usefulness = df['Q228'].values[1: len(df['Q228'].values)]
    screen_suggestion_usefulness_list = []
    for screen_response in screen_suggestion_usefulness:
        screen_suggestion_usefulness_list.append(screen_response)

    OB_understanding = df['Q233'].values[1: len(df['Q233'].values)]
    OB_understanding_list = []
    for OB_response in OB_understanding:
        OB_understanding_list.append(OB_response)

    EB_understanding = df['Q235'].values[1: len(df['Q235'].values)]
    EB_understanding_list = []
    for EB_response in EB_understanding:
        EB_understanding_list.append(EB_response)

    S2R_understanding = df['Q237'].values[1: len(df['Q237'].values)]
    S2R_understanding_list = []
    for S2R_response in S2R_understanding:
        S2R_understanding_list.append(S2R_response)

    BURT_messages_understanding = df['Q240'].values[1: len(df['Q240'].values)]
    BURT_messages_understanding_list = []
    for BURT_message in S2R_understanding:
        BURT_messages_understanding_list.append(BURT_message)

    S2R_panel_usefulness = df['Q244'].values[1: len(df['Q244'].values)]
    S2R_panel_usefulness_list = []
    for panel_response in S2R_panel_usefulness:
        S2R_panel_usefulness_list.append(panel_response)

    BURT_overall_usefulness = df['Q250'].values[1: len(df['Q250'].values)]
    BURT_overall_usefulness_list = []
    for overall_response in BURT_overall_usefulness:
        BURT_overall_usefulness_list.append(overall_response)

    likert_scale_frequency = ['Always', 'Often', 'Sometimes', 'Rarely', 'Never']
    likert_scale_usefulness = ['Useful', 'Somewhat useful', 'Neither useful nor useless', 'Somehow useless', 'Useless']
    likert_scale_easiness = ['Easy to use', 'Somewhat easy to use', 'Neither easy nor difficult to use',
                             'Somewhat difficult to use', 'Difficult to use']

    # source_data = generate_chart_frequency([{"screen_suggestion_usefulness": screen_suggestion_usefulness_list},
    #                                         {"OB_understanding": OB_understanding_list},
    #                                         {"EB_understanding": EB_understanding_list},
    #                                         {"S2R_understanding": S2R_understanding_list},
    #                                         {"BURT_messages_understanding": BURT_messages_understanding_list}],
    #                                        likert_scale_frequency)

    # source_data = generate_chart_frequency([{"BURT_easy_to_use": BURT_overall_usefulness_list}],
    #                                        likert_scale_easiness)

    source_data = generate_chart_frequency([{"S2R_panel_usefulness": S2R_panel_usefulness_list}],
                                           likert_scale_usefulness)

    source = alt.pd.DataFrame(source_data)
    color_scale = alt.Scale(
        # domain=[
        #     "Never",
        #     "Rarely",
        #     "Sometimes",
        #     "Often",
        #     "Always"
        # ],

        # domain=[
        #     'Difficult to use',
        #     'Somewhat difficult to use',
        #     'Neither easy nor difficult to use',
        #     'Somewhat easy to use',
        #     'Easy to use',
        # ],
        #
        domain=[
            'Useless',
            'Somehow useless',
            'Neither useful nor useless',
            'Somewhat useful',
            'Useful',
            ],

        range=["#F4D03F", "#689F38", "#CD6155", "#E67E22", "#21618C"]
    )

    # y_axis = alt.Axis(
    #     title='Question',
    #     offset=5,
    #     ticks=False,
    #     minExtent=60,
    #     domain=False
    # )
    #
    # bars = alt.Chart().mark_bar().encode(
    #     x=alt.X('percentage_start:Q', axis=None),
    #     x2=alt.X2('percentage_end:Q'),
    #     y=alt.Y('question:N', axis=y_axis),
    #     color=alt.Color(
    #         'type:N',
    #         legend=alt.Legend(title='Frequency'),
    #         scale=color_scale,
    #     )
    #
    # ).properties(
    #     title={'text': 'How often'}
    # )
    # text = alt.Chart().mark_text(align='center', baseline='middle').encode(
    #     y=alt.Y('question:N', title=None),
    #     x='position',
    #     text='numbers')
    #
    # alt.layer(bars, text, data=source).show()
    print(source_data)
    Response_order = [
                         'Never',
                         'Rarely',
                         'Sometimes',
                         'Often',
                         'Always'
                     ],
    bars = alt.Chart(source).mark_bar().encode(
        x=alt.X('percentage_start:Q', axis=None),
        x2=alt.X2('percentage_end:Q'),
        # x=alt.X('sum(value):Q', stack='zero'),
        y=alt.Y('question:N'),
        color=alt.Color(
            'type:N',
            legend=alt.Legend(title='Frequency'),
            scale=color_scale,
        ),

        # order=alt.Order('color_Response_sort_index:Q'),
    ).properties(
        title={'text': 'Usefulness of BURT'}
    )

    text = alt.Chart(source).mark_text(align='center', baseline='middle', color='white', fontSize=8).encode(
        # x=alt.X('percentage_start:Q', axis=None),
        # x2=alt.X2('percentage_end:Q'),
        # x=alt.X('sum(value):Q', stack='zero'),
        y=alt.Y('question:N'),
        x='position',
        text='numbers',
        # detail='type:N',
        # text=alt.Text('sum(value):Q'),
        order=alt.Order('color_Response_sort_index:Q'),
    )
    alt.layer(bars, text).show()
