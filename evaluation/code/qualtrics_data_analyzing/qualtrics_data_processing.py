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
    percentage_list = []

    # first, compute percentage for "Sometimes"
    result_sometimes = {"question": question_name, "type": likert_scale_frequency[2]}
    count = answer_list.count(likert_scale_frequency[2])
    percentage_sometimes = count / total_number_of_answer
    result_sometimes["percentage_start"] = -percentage_sometimes / 2
    result_sometimes["percentage_end"] = percentage_sometimes / 2
    percentage_start = -percentage_sometimes / 2
    percentage_start_2 = percentage_sometimes / 2

   

    # second, compute percentage for 'Rarely', 'Never'
    for i in range(3, 5):
        result = {"question": question_name, "type":  likert_scale_frequency[i]}
        count = answer_list.count(likert_scale_frequency[i])
        percentage = count / total_number_of_answer
        result["percentage_start"] = percentage_start - percentage
        result["percentage_end"] = percentage_start
        percentage_start = result["percentage_start"]
        results.append(result)
    results.append(result_sometimes)

    # third, compute percentage for 'Always', 'Often'
    for j in range(1, -1, -1):
        result = {"question": question_name, "type": likert_scale_frequency[j]}
        count = answer_list.count(likert_scale_frequency[j])
        percentage = count / total_number_of_answer
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

    # source = alt.pd.DataFrame([
    #     {
    #         "question": "Question 1",
    #         "type": "Strongly disagree",
    #         "value": 24,
    #         "percentage": 0.7,
    #         "percentage_start": -19.1,
    #         "percentage_end": -18.4
    #     },
    #     {
    #         "question": "Question 1",
    #         "type": "Disagree",
    #         "value": 294,
    #         "percentage": 9.1,
    #         "percentage_start": -18.4,
    #         "percentage_end": -9.2
    #     },
    #     {
    #         "question": "Question 1",
    #         "type": "Neither agree nor disagree",
    #         "value": 594,
    #         "percentage": 18.5,
    #         "percentage_start": -9.2,
    #         "percentage_end": 9.2
    #     },
    #     {
    #         "question": "Question 1",
    #         "type": "Agree",
    #         "value": 1927,
    #         "percentage": 59.9,
    #         "percentage_start": 9.2,
    #         "percentage_end": 69.2
    #     },
    #     {
    #         "question": "Question 1",
    #         "type": "Strongly agree",
    #         "value": 376,
    #         "percentage": 11.7,
    #         "percentage_start": 69.2,
    #         "percentage_end": 80.9
    #     },
    #
    #     {
    #         "question": "Question 2",
    #         "type": "Strongly disagree",
    #         "value": 2,
    #         "percentage": 18.2,
    #         "percentage_start": -36.4,
    #         "percentage_end": -18.2
    #     },
    #     {
    #         "question": "Question 2",
    #         "type": "Disagree",
    #         "value": 2,
    #         "percentage": 18.2,
    #         "percentage_start": -18.2,
    #         "percentage_end": 0
    #     },
    #     {
    #         "question": "Question 2",
    #         "type": "Neither agree nor disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": 0,
    #         "percentage_end": 0
    #     },
    #     {
    #         "question": "Question 2",
    #         "type": "Agree",
    #         "value": 7,
    #         "percentage": 63.6,
    #         "percentage_start": 0,
    #         "percentage_end": 63.6
    #     },
    #     {
    #         "question": "Question 2",
    #         "type": "Strongly agree",
    #         "value": 11,
    #         "percentage": 0,
    #         "percentage_start": 63.6,
    #         "percentage_end": 63.6
    #     },
    #
    #     {
    #         "question": "Question 3",
    #         "type": "Strongly disagree",
    #         "value": 2,
    #         "percentage": 20,
    #         "percentage_start": -30,
    #         "percentage_end": -10
    #     },
    #     {
    #         "question": "Question 3",
    #         "type": "Disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": -10,
    #         "percentage_end": -10
    #     },
    #     {
    #         "question": "Question 3",
    #         "type": "Neither agree nor disagree",
    #         "value": 2,
    #         "percentage": 20,
    #         "percentage_start": -10,
    #         "percentage_end": 10
    #     },
    #     {
    #         "question": "Question 3",
    #         "type": "Agree",
    #         "value": 4,
    #         "percentage": 40,
    #         "percentage_start": 10,
    #         "percentage_end": 50
    #     },
    #     {
    #         "question": "Question 3",
    #         "type": "Strongly agree",
    #         "value": 2,
    #         "percentage": 20,
    #         "percentage_start": 50,
    #         "percentage_end": 70
    #     },
    #
    #     {
    #         "question": "Question 4",
    #         "type": "Strongly disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": -15.6,
    #         "percentage_end": -15.6
    #     },
    #     {
    #         "question": "Question 4",
    #         "type": "Disagree",
    #         "value": 2,
    #         "percentage": 12.5,
    #         "percentage_start": -15.6,
    #         "percentage_end": -3.1
    #     },
    #     {
    #         "question": "Question 4",
    #         "type": "Neither agree nor disagree",
    #         "value": 1,
    #         "percentage": 6.3,
    #         "percentage_start": -3.1,
    #         "percentage_end": 3.1
    #     },
    #     {
    #         "question": "Question 4",
    #         "type": "Agree",
    #         "value": 7,
    #         "percentage": 43.8,
    #         "percentage_start": 3.1,
    #         "percentage_end": 46.9
    #     },
    #     {
    #         "question": "Question 4",
    #         "type": "Strongly agree",
    #         "value": 6,
    #         "percentage": 37.5,
    #         "percentage_start": 46.9,
    #         "percentage_end": 84.4
    #     },
    #
    #     {
    #         "question": "Question 5",
    #         "type": "Strongly disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": -10.4,
    #         "percentage_end": -10.4
    #     },
    #     {
    #         "question": "Question 5",
    #         "type": "Disagree",
    #         "value": 1,
    #         "percentage": 4.2,
    #         "percentage_start": -10.4,
    #         "percentage_end": -6.3
    #     },
    #     {
    #         "question": "Question 5",
    #         "type": "Neither agree nor disagree",
    #         "value": 3,
    #         "percentage": 12.5,
    #         "percentage_start": -6.3,
    #         "percentage_end": 6.3
    #     },
    #     {
    #         "question": "Question 5",
    #         "type": "Agree",
    #         "value": 16,
    #         "percentage": 66.7,
    #         "percentage_start": 6.3,
    #         "percentage_end": 72.9
    #     },
    #     {
    #         "question": "Question 5",
    #         "type": "Strongly agree",
    #         "value": 4,
    #         "percentage": 16.7,
    #         "percentage_start": 72.9,
    #         "percentage_end": 89.6
    #     },
    #
    #     {
    #         "question": "Question 6",
    #         "type": "Strongly disagree",
    #         "value": 1,
    #         "percentage": 6.3,
    #         "percentage_start": -18.8,
    #         "percentage_end": -12.5
    #     },
    #     {
    #         "question": "Question 6",
    #         "type": "Disagree",
    #         "value": 1,
    #         "percentage": 6.3,
    #         "percentage_start": -12.5,
    #         "percentage_end": -6.3
    #     },
    #     {
    #         "question": "Question 6",
    #         "type": "Neither agree nor disagree",
    #         "value": 2,
    #         "percentage": 12.5,
    #         "percentage_start": -6.3,
    #         "percentage_end": 6.3
    #     },
    #     {
    #         "question": "Question 6",
    #         "type": "Agree",
    #         "value": 9,
    #         "percentage": 56.3,
    #         "percentage_start": 6.3,
    #         "percentage_end": 62.5
    #     },
    #     {
    #         "question": "Question 6",
    #         "type": "Strongly agree",
    #         "value": 3,
    #         "percentage": 18.8,
    #         "percentage_start": 62.5,
    #         "percentage_end": 81.3
    #     },
    #
    #     {
    #         "question": "Question 7",
    #         "type": "Strongly disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": -10,
    #         "percentage_end": -10
    #     },
    #     {
    #         "question": "Question 7",
    #         "type": "Disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": -10,
    #         "percentage_end": -10
    #     },
    #     {
    #         "question": "Question 7",
    #         "type": "Neither agree nor disagree",
    #         "value": 1,
    #         "percentage": 20,
    #         "percentage_start": -10,
    #         "percentage_end": 10
    #     },
    #     {
    #         "question": "Question 7",
    #         "type": "Agree",
    #         "value": 4,
    #         "percentage": 80,
    #         "percentage_start": 10,
    #         "percentage_end": 90
    #     },
    #     {
    #         "question": "Question 7",
    #         "type": "Strongly agree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": 90,
    #         "percentage_end": 90
    #     },
    #
    #     {
    #         "question": "Question 8",
    #         "type": "Strongly disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": 0,
    #         "percentage_end": 0
    #     },
    #     {
    #         "question": "Question 8",
    #         "type": "Disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": 0,
    #         "percentage_end": 0
    #     },
    #     {
    #         "question": "Question 8",
    #         "type": "Neither agree nor disagree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": 0,
    #         "percentage_end": 0
    #     },
    #     {
    #         "question": "Question 8",
    #         "type": "Agree",
    #         "value": 0,
    #         "percentage": 0,
    #         "percentage_start": 0,
    #         "percentage_end": 0
    #     },
    #     {
    #         "question": "Question 8",
    #         "type": "Strongly agree",
    #         "value": 2,
    #         "percentage": 100,
    #         "percentage_start": 0,
    #         "percentage_end": 100
    #     }
    # ])
    #
    # color_scale = alt.Scale(
    #     domain=[
    #         "Strongly disagree",
    #         "Disagree",
    #         "Neither agree nor disagree",
    #         "Agree",
    #         "Strongly agree"
    #     ],
    #     range=["#c30d24", "#f3a583", "#cccccc", "#94c6da", "#1770ab"]
    # )
    #
    # y_axis = alt.Axis(
    #     title='Question',
    #     offset=5,
    #     ticks=False,
    #     minExtent=60,
    #     domain=False
    # )
    #
    # alt.Chart(source).mark_bar().encode(
    #     x='percentage_start:Q',
    #     x2='percentage_end:Q',
    #     y=alt.Y('question:N', axis=y_axis),
    #     color=alt.Color(
    #         'type:N',
    #         legend=alt.Legend(title='Response'),
    #         scale=color_scale,
    #     )
    # ).show()
