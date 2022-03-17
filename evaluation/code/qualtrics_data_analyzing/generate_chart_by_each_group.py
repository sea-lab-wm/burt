import os
from pathlib import Path

import pandas
from matplotlib.colors import LinearSegmentedColormap

"""
=============================================
Discrete distribution as horizontal bar chart
=============================================

Stacked bar charts can be used to visualize discrete distributions.

This example visualizes the result of a survey in which people could rate
their agreement to questions on a five-element scale.

The horizontal stacking is achieved by calling `~.Axes.barh()` for each
category and passing the starting point as the cumulative sum of the
already drawn bars via the parameter ``left``.
"""

import numpy as np
import matplotlib.pyplot as plt


def survey_usefulness(results, category_names, experience_level):
    """
    Parameters
    ----------
    results : dict
        A mapping from question labels to a list of answers per category.
        It is assumed all lists contain the same number of entries and that
        it matches the length of *category_names*.
    category_names : list of str
        The category labels.
    """
    labels = list(results.keys())
    data = np.array(list(results.values()))
    data_cum = data.cumsum(axis=1)
    category_colors = plt.get_cmap('tab20c')(
        np.array([0.3, 0.35, 1, 0.15, 0.05]))
    fig = plt.figure(figsize=(8, 4))
    ax = fig.add_axes([0.15, 0.3, 0.7, 0.4])  # easy to use

    # fig, ax = plt.subplots(figsize=(8, 2))
    # ax = fig.add_axes([0.15, 0.4, 0.7, 0.4])
    ax.invert_yaxis()
    ax.xaxis.set_visible(False)
    ax.set_xlim(0, np.sum(data, axis=1).max())

    for i, (colname, color) in enumerate(zip(category_names, category_colors)):
        widths = data[:, i]

        starts = data_cum[:, i] - widths

        rects = ax.barh(labels, widths, left=starts, height=0.6,
                        label=colname, color=color)

        labels1 = [f'{w:.0f}' if (w := v) > 0 else '' for v in widths]
        r, g, b, _ = color
        text_color = 'black'
        ax.bar_label(rects, labels=labels1, label_type='center', color=text_color, fontweight='bold', fontsize='large')
    legend_properties = {'weight': 'bold'}
    ax.legend(ncol=5, bbox_to_anchor=(-0.08, -0.25),
              loc='lower left', fontsize="medium", prop=legend_properties)
    path_output = os.path.join("result", experience_level)
    Path(path_output).mkdir(parents=True, exist_ok=True)
    plt.savefig(path_output + '/usefulness.pdf', format="pdf")
    plt.savefig(path_output + '/usefulness.png', dpi=300)
    return fig, ax


def survey_easy_to_use(results, category_names, experience_level):
    """
    Parameters
    ----------
    results : dict
        A mapping from question labels to a list of answers per category.
        It is assumed all lists contain the same number of entries and that
        it matches the length of *category_names*.
    category_names : list of str
        The category labels.
    """
    labels = list(results.keys())
    data = np.array(list(results.values()))
    data_cum = data.cumsum(axis=1)
    category_colors = plt.get_cmap('tab20c')(
        np.array([0.3, 0.35, 1, 0.15, 0.05]))

    fig = plt.figure(figsize=(8, 2))  # width, height
    ax = fig.add_axes([0.04, 0.4, 0.9, 0.4])  # easy to use  [left, bottom, width, height]

    ax.invert_yaxis()
    ax.xaxis.set_visible(False)
    ax.yaxis.set_visible(False)
    ax.set_xlim(0, np.sum(data, axis=1).max())
    ax.set_ylim([-0.6, 0.6])

    for i, (colname, color) in enumerate(zip(category_names, category_colors)):
        widths = data[:, i]

        starts = data_cum[:, i] - widths
        rects = ax.barh(labels, widths, left=starts, height=0.4,
                        label=colname, color=color)
        labels1 = [f'{w:.0f}' if (w := v) > 0 else '' for v in widths]
        r, g, b, _ = color
        text_color = 'black'
        ax.bar_label(rects, labels=labels1, label_type='center', color=text_color, fontweight='bold', fontsize='large')
    legend_properties = {'weight': 'bold'}
    ax.legend(ncol=5, bbox_to_anchor=(-0.025, -0.55),
              loc='lower left', fontsize="medium", prop=legend_properties)

    plt.subplots_adjust(left=0, bottom=0, right=1, top=10)
    path_output = os.path.join("result", experience_level)
    Path(path_output).mkdir(parents=True, exist_ok=True)
    plt.savefig(path_output + '/easytouse.pdf', format="pdf")
    plt.savefig(path_output + '/easytouse.png', dpi=300)

    return fig, ax


def survey_panel_to_use(results, category_names, experience_level):
    """
    Parameters
    ----------
    results : dict
        A mapping from question labels to a list of answers per category.
        It is assumed all lists contain the same number of entries and that
        it matches the length of *category_names*.
    category_names : list of str
        The category labels.
    """
    labels = list(results.keys())
    data = np.array(list(results.values()))
    data_cum = data.cumsum(axis=1)
    category_colors = plt.get_cmap('tab20c')(
        np.array([0.3, 0.35, 1, 0.15, 0.05]))

    fig = plt.figure(figsize=(8, 2))  # width, height
    ax = fig.add_axes([0.04, 0.4, 0.9, 0.4])  # easy to use  [left, bottom, width, height]

    ax.invert_yaxis()
    ax.xaxis.set_visible(False)
    ax.yaxis.set_visible(False)
    ax.set_xlim(0, np.sum(data, axis=1).max())
    ax.set_ylim([-0.6, 0.6])

    for i, (colname, color) in enumerate(zip(category_names, category_colors)):
        widths = data[:, i]

        starts = data_cum[:, i] - widths
        rects = ax.barh(labels, widths, left=starts, height=0.4,
                        label=colname, color=color)
        labels1 = [f'{w:.0f}' if (w := v) > 0 else '' for v in widths]
        r, g, b, _ = color
        text_color = 'black'
        ax.bar_label(rects, labels=labels1, label_type='center', color=text_color, fontweight='bold', fontsize='large')
    legend_properties = {'weight': 'bold'}
    ax.legend(ncol=5, bbox_to_anchor=(-0.03, -0.55),
              loc='lower left', fontsize="medium", prop=legend_properties)
    path_output = os.path.join("result", experience_level)
    Path(path_output).mkdir(parents=True, exist_ok=True)
    plt.savefig(path_output + '/panel.pdf', format="pdf")
    plt.savefig(path_output + '/panel.png', dpi=300)
    return fig, ax


def generate_json_for_each_question(question_dict, likert_scale_frequency):
    #  likert_scale_frequency = ['Always', 'Often', 'Sometimes', 'Rarely', 'Never]
    result = []
    for key, value in question_dict.items():
        question_name = key
        answer_list = value

    for response in likert_scale_frequency:
        count = answer_list.count(response)
        result.append(count)
    return question_name, result


def generate_chart_frequency(question_list, likert_scale_frequency):
    source_data = {}
    for i in range(len(question_list)):
        question_name, result = generate_json_for_each_question(question_list[i], likert_scale_frequency)
        source_data[question_name] = result
    return source_data


if __name__ == '__main__':
    df = pandas.read_excel('../BURT ICSE’22 Evaluation Survey_August_18_users.xlsx')


    # filter by experience
    # df = df[df['Q0'] == 'technical with reporting']
    # df = df.loc[df['Q0'].str.strip() == 'technical without reporting']

    experience_list = ['non-technical without reporting', 'technical with reporting', 'technical without reporting']
    df_list = [df[df['Q0'].str.strip() == 'non-technical without reporting'], df.loc[df['Q0'].str.strip() == 'technical without reporting'], df[df['Q0'] == 'technical with reporting']]
    for i in range(len(df_list)):
        df = df_list[i]
        screen_suggestion_usefulness = df['Q228'].values[0: len(df['Q228'].values)]
        print(screen_suggestion_usefulness)

        screen_suggestion_usefulness_list = []
        for screen_response in screen_suggestion_usefulness:
            screen_suggestion_usefulness_list.append(screen_response)

        OB_understanding = df['Q233'].values[0: len(df['Q233'].values)]
        OB_understanding_list = []
        for OB_response in OB_understanding:
            OB_understanding_list.append(OB_response)

        EB_understanding = df['Q235'].values[0: len(df['Q235'].values)]
        EB_understanding_list = []
        for EB_response in EB_understanding:
            EB_understanding_list.append(EB_response)

        S2R_understanding = df['Q237'].values[0: len(df['Q237'].values)]
        S2R_understanding_list = []
        for S2R_response in S2R_understanding:
            S2R_understanding_list.append(S2R_response)

        BURT_messages_understanding = df['Q240'].values[0: len(df['Q240'].values)]
        BURT_messages_understanding_list = []
        for BURT_message in S2R_understanding:
            BURT_messages_understanding_list.append(BURT_message)

        S2R_panel_usefulness = df['Q244'].values[0: len(df['Q244'].values)]
        S2R_panel_usefulness_list = []
        for panel_response in S2R_panel_usefulness:
            S2R_panel_usefulness_list.append(panel_response)

        BURT_overall_easy_to_use = df['Q250'].values[0: len(df['Q250'].values)]
        BURT_overall_easy_to_use_list = []
        for overall_response in BURT_overall_easy_to_use:
            if overall_response == 'Neither easy nor difficult to use':
                BURT_overall_easy_to_use_list.append("Neutral")
            else:
                BURT_overall_easy_to_use_list.append(overall_response.replace(" to use", ""))

        likert_scale_frequency = ['Never', 'Rarely', 'Sometimes', 'Often', 'Always']

        likert_scale_usefulness = ['Useless', 'Somehow useless', 'Neutral', 'Somewhat useful', 'Useful']
        likert_scale_easiness = ['Difficult', 'Somewhat difficult', 'Neutral',
                                 'Somewhat easy', 'Easy', ]
        # useful
        source_data = generate_chart_frequency([{"Screens": screen_suggestion_usefulness_list},
                                                {"OB": OB_understanding_list},
                                                {"EB": EB_understanding_list},
                                                {"S2Rs": S2R_understanding_list},
                                                {"Messages": BURT_messages_understanding_list}],
                                               likert_scale_frequency)
        survey_usefulness(source_data, likert_scale_frequency, experience_list[i])

        # easy to use
        source_data = generate_chart_frequency([{"": BURT_overall_easy_to_use_list}],
                                               likert_scale_easiness)
        survey_easy_to_use(source_data, likert_scale_easiness, experience_list[i])

        # panel
        source_data = generate_chart_frequency([{"": S2R_panel_usefulness_list}],
                                               likert_scale_usefulness)
        survey_panel_to_use(source_data, likert_scale_usefulness, experience_list[i])

        plt.show()
        plt.close("all")

#############################################################################
#
# .. admonition:: References
#
#    The use of the following functions, methods, classes and modules is shown
#    in this example:
#
#    - `matplotlib.axes.Axes.barh` / `matplotlib.pyplot.barh`
#    - `matplotlib.axes.Axes.bar_label` / `matplotlib.pyplot.bar_label`
#    - `matplotlib.axes.Axes.legend` / `matplotlib.pyplot.legend`
