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

    # first, compute percentage for "Sometimes"
    result_sometimes = {"question": question_name, "type": likert_scale_frequency[2]}
    count = answer_list.count(likert_scale_frequency[2])
    result_sometimes["value"] = count
    percentage_sometimes = count / total_number_of_answer
    result_sometimes["percentage"] = percentage_sometimes * 100
    result_sometimes["percentage_start"] = -percentage_sometimes / 2 * 100
    result_sometimes["percentage_end"] = percentage_sometimes / 2 * 100
    percentage_start = -percentage_sometimes / 2 * 100
    percentage_start_2 = percentage_sometimes / 2 * 100
    results.append(result_sometimes)
    # second, compute percentage for 'Rarely', 'Never'
    for i in range(3, 5):
        result = {"question": question_name, "type": likert_scale_frequency[i]}
        count = answer_list.count(likert_scale_frequency[i])
        percentage = count / total_number_of_answer * 100
        result["value"] = count
        result["percentage"] = percentage
        result["percentage_start"] = percentage_start - percentage
        result["percentage_end"] = percentage_start
        percentage_start = result["percentage_start"]
        results.append(result)

    # third, compute percentage for 'Always', 'Often'
    for j in range(1, -1, -1):
        result = {"question": question_name, "type": likert_scale_frequency[j]}
        count = answer_list.count(likert_scale_frequency[j])
        percentage = count / total_number_of_answer * 100
        result["value"] = count
        result["percentage"] = percentage
        result["percentage_start"] = percentage_start_2
        result["percentage_end"] = percentage_start_2 + percentage
        percentage_start_2 = result["percentage_end"]
        results.append(result)

    print(results)
    return results


def generate_chart_frequency(question_list, likert_scale_frequency):
    source_data_list = []
    for question in question_list:
        source_data_list.extend(generate_json_for_each_question(question, likert_scale_frequency))
    return source_data_list


if __name__ == '__main__':
    df = pandas.read_excel('../BURT ICSE’22 Evaluation Survey_seven_users.xlsx')

    screen_suggestion_usefulness = df['Q228'].values[1: len(df['Q228'].values)]
    screen_suggestion_usefulness_list = []
    for screen in screen_suggestion_usefulness:
        screen_suggestion_usefulness_list.append(screen)

    OB_understanding = df['Q233'].values[1: len(df['Q233'].values)]
    OB_understanding_list = []
    for OB in OB_understanding:
        OB_understanding_list.append(OB)

    EB_understanding = df['Q235'].values[1: len(df['Q235'].values)]
    EB_understanding_list = []
    for EB in EB_understanding:
        EB_understanding_list.append(EB)

    S2R_understanding = df['Q237'].values[1: len(df['Q237'].values)]
    S2R_understanding_list = []
    for S2R in S2R_understanding:
        S2R_understanding_list.append(S2R)

    BURT_messages_understanding = df['Q240'].values[1: len(df['Q240'].values)]
    BURT_messages_understanding_list = []
    for BURT_message in S2R_understanding:
        BURT_messages_understanding_list.append(BURT_message)

    S2R_panel_usefulness = df['Q244'].values[1: len(df['Q244'].values)]
    BURT_overall_usefulness = df['Q250'].values[1: len(df['Q250'].values)]

    likert_scale_frequency = ['Always', 'Often', 'Sometimes', 'Rarely', 'Never']
    likert_scale_usefulness = ['Useful', 'Somewhat useful', 'Neither useful nor useless', 'Somehow useless', 'Useless']
    likert_scale_easiness = ['Easy to use', 'Somewhat easy to use', 'Neither easy nor difficult to use',
                             'Somewhat difficult to use', 'Difficult to use']

    source_data = generate_chart_frequency([{"screen_suggestion_usefulness": screen_suggestion_usefulness_list},
                                            {"OB_understanding": OB_understanding_list},
                                            {"EB_understanding": EB_understanding_list},
                                            {"S2R_understanding": S2R_understanding_list},
                                            {"BURT_messages_understanding": BURT_messages_understanding_list}],
                                           likert_scale_frequency)

    source = alt.pd.DataFrame(source_data)
    color_scale = alt.Scale(
        domain=[
            "Never",
            "Rarely",
            "Sometimes",
            "Often",
            "Always"
        ],
        range=["#c30d24", "#f3a583", "#cccccc", "#94c6da", "#1770ab"]
    )

    y_axis = alt.Axis(
        title='Question',
        offset=5,
        ticks=False,
        minExtent=60,
        domain=False
    )

    alt.Chart(source).mark_bar().encode(
        x='percentage_start:Q',
        x2='percentage_end:Q',
        y=alt.Y('question:N', axis=y_axis),
        color=alt.Color(
            'type:N',
            legend=alt.Legend(title='Response'),
            scale=color_scale,
        )
    ).show()
