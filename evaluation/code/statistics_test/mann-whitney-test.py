from scipy.stats import mannwhitneyu
from scipy.stats import wilcoxon
import xlrd
import pandas as pd
import matplotlib.pyplot as plt
from cliffs_delta import cliffs_delta

res_ob_itrac = [0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 2, 1]
res_ob_burt = [1, 1, 2, 3, 0, 1, 0, 3, 1, 2, 2, 0]
res_eb_itrac = [0, 1, 1, 1, 1, 0, 0, 1, 0, 2, 0, 1]
res_eb_burt = [3, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0]

def get_burt_data():
    sheet_incorrect = pd.read_excel('burt_data.xlsx', usecols=[24], names=None)
    incorrect_list = sheet_incorrect.values.tolist()
    incorrect_steps = []
    for li in incorrect_list:
        incorrect_steps.append(li[0])

    sheet_missing = pd.read_excel('burt_data.xlsx', usecols=[26], names=None)
    missing_list = sheet_missing.values.tolist()
    missing_steps = []
    for li in missing_list:
        missing_steps.append(li[0])

    return incorrect_steps, missing_steps


def get_itrac_data():
    sheet_incorrect = pd.read_excel('itrac_data.xlsx', usecols=[11], names=None)
    incorrect_list = sheet_incorrect.values.tolist()
    incorrect_steps = []
    for li in incorrect_list:
        incorrect_steps.append(li[0])

    sheet_missing = pd.read_excel('itrac_data.xlsx', usecols=[12], names=None)
    missing_list = sheet_missing.values.tolist()
    missing_steps = []
    for li in missing_list:
        missing_steps.append(li[0])

    return incorrect_steps, missing_steps


def get_burt_ob_eb_data():
    sheet_incorrect = pd.read_excel('burt_data.xlsx', usecols=[28], names=None)
    ob_list = sheet_incorrect.values.tolist()
    ob = []
    for li in ob_list:
        ob.append(li[0])

    sheet_incorrect = pd.read_excel('burt_data.xlsx', usecols=[29], names=None)
    eb_list = sheet_incorrect.values.tolist()
    eb = []
    for li in eb_list:
        eb.append(li[0])

    return ob, eb


def get_itrac_ob_eb_data():
    sheet_incorrect = pd.read_excel('itrac_data.xlsx', usecols=[7], names=None)
    ob_list = sheet_incorrect.values.tolist()
    ob = []
    for li in ob_list:
        ob.append(li[0])

    sheet_incorrect = pd.read_excel('itrac_data.xlsx', usecols=[8], names=None)
    eb_list = sheet_incorrect.values.tolist()
    eb = []
    for li in eb_list:
        eb.append(li[0])

    return ob, eb


def get_burt_data_per_bug():
    sheet_incorrect = pd.read_excel('burt_data_1.xlsx', usecols=[0], names=None)
    incorrect_list = sheet_incorrect.values.tolist()
    incorrect_steps = []
    for li in incorrect_list:
        incorrect_steps.append(li[0])

    sheet_missing = pd.read_excel('burt_data_1.xlsx', usecols=[1], names=None)
    missing_list = sheet_missing.values.tolist()
    missing_steps = []
    for li in missing_list:
        missing_steps.append(li[0])

    return incorrect_steps, missing_steps


def get_itrac_data_per_bug():
    sheet_incorrect = pd.read_excel('itrac_data_1.xlsx', usecols=[0], names=None)
    incorrect_list = sheet_incorrect.values.tolist()
    incorrect_steps = []
    for li in incorrect_list:
        incorrect_steps.append(li[0])

    sheet_missing = pd.read_excel('itrac_data_1.xlsx', usecols=[1], names=None)
    missing_list = sheet_missing.values.tolist()
    missing_steps = []
    for li in missing_list:
        missing_steps.append(li[0])

    return incorrect_steps, missing_steps


def draw_box_plots(data1, data2, data3, data4):
    all_data = []
    all_data.append(data1)
    all_data.append(data2)
    all_data.append(data3)
    all_data.append(data4)
    fig, axes = plt.subplots(nrows=1, ncols=2, figsize=(9, 4))
    bplot1 = axes[0].boxplot(all_data[0:2],
                             vert=True,
                             patch_artist=True, showfliers=False, showmeans=True, meanline=False)
    # showmeans=True, meanline=True,

    bplot2 = axes[1].boxplot(all_data[2:4],
                             # notch=True,
                             vert=True,
                             patch_artist=True, showfliers=False, showmeans=True, meanline=False)
    colors = ['pink', 'lightblue']
    for bplot in (bplot1, bplot2):
        for patch, color in zip(bplot['boxes'], colors):
            patch.set_facecolor(color)
    plt.setp(axes, xticks=[1, 2],
             xticklabels=['burt', 'itrac'])

    # axes[0].set_title("incorrect steps", fontsize=15)
    # axes[1].set_title("missing steps", fontsize=15)
    axes[0].set_title("OB", fontsize=15)
    axes[1].set_title("EB", fontsize=15)
    plt.show()


def compute_mann_whiteney():
    burt_incorrect_steps, burt_missing_steps = get_burt_data()
    itrac_incorrect_steps, itrac_missing_steps = get_itrac_data()
    burt_ob, burt_eb = get_burt_ob_eb_data()
    itrac_ob, itrac_eb = get_itrac_ob_eb_data()
    burt_incorrect_steps_per_bug, burt_missing_steps_per_bug = get_burt_data_per_bug()
    itrac_incorrect_steps_per_bug, itrac_missing_steps_per_bug = get_itrac_data_per_bug()

    # res_missing = mannwhitneyu(burt_missing_steps, itrac_missing_steps, alternative="less", method="auto")
    #
    # print(res_missing)
    #
    # res_incorrect = mannwhitneyu(burt_incorrect_steps, itrac_incorrect_steps, alternative="less", method="auto")
    #
    # print(res_incorrect)
    #
    # res_ob = mannwhitneyu(burt_ob, itrac_ob, alternative="less", method="auto")
    #
    # print(res_ob)
    #
    # res_eb = mannwhitneyu(burt_eb, itrac_eb, alternative="less", method="auto")

    # print(res_eb)

    res_ob_per_bug = mannwhitneyu(res_ob_burt, res_ob_itrac, alternative="less", method="auto")

    print("ob", res_ob_per_bug)

    res_eb_per_bug = mannwhitneyu(res_eb_burt, res_eb_itrac, alternative="less", method="auto")

    print("eb", res_eb_per_bug)

    res_missing_1 = mannwhitneyu(burt_missing_steps_per_bug, itrac_missing_steps_per_bug, alternative="less", method="auto")

    print("missing_steps_per_bug", res_missing_1)

    res_incorrect_1 = mannwhitneyu(burt_incorrect_steps_per_bug, itrac_incorrect_steps_per_bug, alternative="less", method="auto")

    print("incorrect_steps_per_bug", res_incorrect_1)


def compute_cliffs_delta(data1, data2):
    d, res = cliffs_delta(data1, data2)
    return d, res


def compute_cliff_delta_all():
    res_incorrect_steps_per_bug, d_incorrect_steps_per_bug = compute_cliffs_delta(burt_incorrect_steps_per_bug, itrac_incorrect_steps_per_bug)
    print("incorrect steps per bug", res_incorrect_steps_per_bug, d_incorrect_steps_per_bug)

    res_missing_steps_per_bug, d_missing_steps_per_bug = compute_cliffs_delta(burt_missing_steps_per_bug, itrac_missing_steps_per_bug)
    print("missing steps per bug", res_missing_steps_per_bug, d_missing_steps_per_bug)

    # res_incorrect_steps_per_bug_report, d_incorrect_steps_per_bug_report = compute_cliffs_delta(burt_incorrect_steps_per_bug_report, itrac_incorrect_steps_per_bug_report)
    # print("incorrect steps per bug report", res_incorrect_steps_per_bug_report, d_incorrect_steps_per_bug_report)
    #
    # res_missing_steps_per_bug_report, d_missing_steps_per_bug_report = compute_cliffs_delta(burt_missing_steps_per_bug_report, itrac_missing_steps_per_bug_report)
    # print("missing steps per bug report", res_missing_steps_per_bug_report, d_missing_steps_per_bug_report)

    res_ob_per_bug, d_ob_per_bug = compute_cliffs_delta(res_ob_burt, res_ob_itrac)
    print("ob per bug", res_ob_per_bug, d_ob_per_bug)

    res_eb_per_bug, d_eb_per_bug = compute_cliffs_delta(res_eb_burt, res_eb_itrac)
    print("eb per bug", res_eb_per_bug, d_eb_per_bug)


def get_bug_experience_data(file_name):
    sheet_incorrect_novice = pd.read_excel(file_name, usecols=[0], names=None)
    incorrect_steps_list_novice = sheet_incorrect_novice.values.tolist()
    incorrect_steps_novice = []
    for li in incorrect_steps_list_novice:
        incorrect_steps_novice.append(li[0])

    sheet_missing_novice = pd.read_excel(file_name, usecols=[1], names=None)
    missing_list_novice = sheet_missing_novice.values.tolist()
    missing_steps_novice = []
    for li in missing_list_novice:
        missing_steps_novice.append(li[0])


    sheet_incorrect_intermediate = pd.read_excel(file_name, usecols=[2], names=None)
    incorrect_steps_list_intermediate = sheet_incorrect_intermediate.values.tolist()
    incorrect_steps_intermediate = []
    for li in incorrect_steps_list_intermediate:
        incorrect_steps_intermediate.append(li[0])

    sheet_missing_intermediate = pd.read_excel(file_name, usecols=[3], names=None)
    missing_list_intermediate = sheet_missing_intermediate.values.tolist()
    missing_steps_intermediate = []
    for li in missing_list_intermediate:
        missing_steps_intermediate.append(li[0])


    sheet_incorrect_experienced = pd.read_excel(file_name, usecols=[4], names=None)
    incorrect_steps_list_experienced = sheet_incorrect_experienced.values.tolist()
    incorrect_steps_experienced = []
    for li in incorrect_steps_list_experienced:
        incorrect_steps_experienced.append(li[0])

    sheet_missing_experienced = pd.read_excel(file_name, usecols=[5], names=None)
    missing_list_experienced = sheet_missing_experienced.values.tolist()
    missing_steps_experienced = []
    for li in missing_list_experienced:
        missing_steps_experienced.append(li[0])


    return incorrect_steps_novice, missing_steps_novice, incorrect_steps_intermediate, \
           missing_steps_intermediate, incorrect_steps_experienced, missing_steps_experienced




def test_bug_experience():
    incorrect_steps_novice_burt, missing_steps_novice_burt, incorrect_steps_intermediate_burt, missing_steps_intermediate_burt, \
    incorrect_steps_experienced_burt, missing_steps_experienced_burt = get_bug_experience_data('bug_reporting_experience_burt.xlsx')

    incorrect_steps_novice_itrac, missing_steps_novice_itrac, incorrect_steps_intermediate_itrac, missing_steps_intermediate_itrac, \
    incorrect_steps_experienced_itrac, missing_steps_experienced_itrac = get_bug_experience_data('bug_reporting_experience_itrac.xlsx')

    incorrect_steps_novice_burt = [x for x in incorrect_steps_novice_burt if str(x) != 'nan']
    missing_steps_novice_burt = [x for x in missing_steps_novice_burt if str(x) != 'nan']
    incorrect_steps_intermediate_burt = [x for x in incorrect_steps_intermediate_burt if str(x) != 'nan']
    missing_steps_intermediate_burt = [x for x in missing_steps_intermediate_burt if str(x) != 'nan']
    incorrect_steps_experienced_burt = [x for x in incorrect_steps_experienced_burt if str(x) != 'nan']
    missing_steps_experienced_burt = [x for x in missing_steps_experienced_burt if str(x) != 'nan']

    incorrect_steps_novice_itrac = [x for x in incorrect_steps_novice_itrac if str(x) != 'nan']
    missing_steps_novice_itrac = [x for x in missing_steps_novice_itrac if str(x) != 'nan']
    incorrect_steps_intermediate_itrac = [x for x in incorrect_steps_intermediate_itrac if str(x) != 'nan']
    missing_steps_intermediate_itrac = [x for x in missing_steps_intermediate_itrac if str(x) != 'nan']
    incorrect_steps_experienced_itrac = [x for x in incorrect_steps_experienced_itrac if str(x) != 'nan']
    missing_steps_experienced_itrac = [x for x in missing_steps_experienced_itrac if str(x) != 'nan']

    print(incorrect_steps_novice_burt)
    print(missing_steps_novice_burt)
    print(incorrect_steps_intermediate_burt)
    print(missing_steps_intermediate_burt)
    print(incorrect_steps_experienced_burt)
    print(missing_steps_experienced_burt)


    print(incorrect_steps_novice_itrac)
    print(missing_steps_novice_itrac)
    print(incorrect_steps_intermediate_itrac)
    print(missing_steps_intermediate_itrac)
    print(incorrect_steps_experienced_itrac)
    print(missing_steps_experienced_itrac)



    res_missing_novice = mannwhitneyu(missing_steps_novice_burt, missing_steps_novice_itrac, alternative="less", method="auto")

    print("res_missing_novice", res_missing_novice)

    res_missing_steps_novice_cliff, d_missing_steps_novice_cliff = compute_cliffs_delta(missing_steps_novice_burt, missing_steps_novice_itrac)
    print("missing_steps_novice_cliff", res_missing_steps_novice_cliff, d_missing_steps_novice_cliff)


    res_incorrect_novice = mannwhitneyu(incorrect_steps_novice_burt, incorrect_steps_novice_itrac, alternative="less", method="auto")

    print("res_incorrect_novice", res_incorrect_novice)

    res_incorrect_novice_cliff, d_incorrect_novice_cliff = compute_cliffs_delta(incorrect_steps_novice_burt, incorrect_steps_novice_itrac)
    print("incorrect_novice_cliff", res_incorrect_novice_cliff, d_incorrect_novice_cliff)


    res_missing_intermediate = mannwhitneyu(missing_steps_intermediate_burt, missing_steps_intermediate_itrac, alternative="less", method="auto")

    print("res_missing_intermediate", res_missing_intermediate)

    res_missing_intermediate_cliff, d_missing_intermediate_cliff = compute_cliffs_delta(missing_steps_intermediate_burt, missing_steps_intermediate_itrac)
    print("missing_intermediate_cliff", res_missing_intermediate_cliff, d_missing_intermediate_cliff)



    res_incorrect_intermediate = mannwhitneyu(incorrect_steps_intermediate_burt, incorrect_steps_intermediate_itrac, alternative="less", method="auto")

    print("res_incorrect_intermediate", res_incorrect_intermediate)

    res_incorrect_intermediate_cliff, d_incorrect_intermediate_cliff = compute_cliffs_delta(incorrect_steps_intermediate_burt, incorrect_steps_intermediate_itrac)
    print("incorrect_intermediate_cliff", res_incorrect_intermediate_cliff, d_incorrect_intermediate_cliff)


    res_missing_experienced = mannwhitneyu(missing_steps_experienced_burt, missing_steps_experienced_itrac, alternative="less", method="auto")

    print("res_missing_experienced", res_missing_experienced)

    res_missing_experienced_cliff, d_missing_experienced_cliff = compute_cliffs_delta(missing_steps_experienced_burt, missing_steps_experienced_itrac)
    print("incorrect_novice_cliff", res_missing_experienced_cliff, d_missing_experienced_cliff)

    res_incorrect_experienced = mannwhitneyu(incorrect_steps_experienced_burt, incorrect_steps_experienced_itrac, alternative="less", method="auto")

    print("res_incorrect_experienced", res_incorrect_experienced)

    res_incorrect_experienced_cliff, d_incorrect_experienced_cliff = compute_cliffs_delta(incorrect_steps_experienced_burt, incorrect_steps_experienced_itrac)
    print("incorrect_incorrect_experienced_cliff", res_incorrect_experienced_cliff, d_incorrect_experienced_cliff)




if __name__ == '__main__':
    # compute_mann_whiteney()
    '''per bug for missing and incorrect steps'''
    burt_incorrect_steps_per_bug, burt_missing_steps_per_bug = get_burt_data_per_bug()
    itrac_incorrect_steps_per_bug, itrac_missing_steps_per_bug = get_itrac_data_per_bug()
    # draw_box_plots(burt_incorrect_steps_per_bug, itrac_incorrect_steps_per_bug,
    #                burt_missing_steps_per_bug, itrac_missing_steps_per_bug)

    '''per bug report for missing and incorrect steps'''
    burt_incorrect_steps_per_bug_report, burt_missing_steps_per_bug_report = get_burt_data()
    itrac_incorrect_steps_per_bug_report, itrac_missing_steps_per_bug_report = get_itrac_data()

    # draw_box_plots(burt_incorrect_steps, itrac_incorrect_steps,
    #                burt_missing_steps, itrac_missing_steps)

    '''per bug report for OB and EB'''

    draw_box_plots(res_ob_burt, res_ob_itrac,
                   res_eb_burt, res_eb_itrac)

    '''compute cliffs delta'''
    compute_cliff_delta_all()

    test_bug_experience()


