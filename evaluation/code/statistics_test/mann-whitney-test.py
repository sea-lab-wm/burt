from scipy.stats import mannwhitneyu
from scipy.stats import wilcoxon
import xlrd
import pandas as pd
import matplotlib.pyplot as plt


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
    #showmeans=True, meanline=True,

    bplot2 = axes[1].boxplot(all_data[2:4],
                             notch=True,
                             vert=True,
                             patch_artist=True, showfliers=False, showmeans=True, meanline=False)
    colors = ['pink', 'lightblue']
    for bplot in (bplot1, bplot2):
        for patch, color in zip(bplot['boxes'], colors):
            patch.set_facecolor(color)
    plt.setp(axes, xticks=[1, 2],
             xticklabels=['burt', 'itrac'])

    axes[0].set_title("incorrect steps", fontsize=15)
    axes[1].set_title("missing steps", fontsize=15)
    plt.show()


def compute_mann_whiteney():
    burt_incorrect_steps, burt_missing_steps = get_burt_data()
    itrac_incorrect_steps, itrac_missing_steps = get_itrac_data()
    burt_ob, burt_eb = get_burt_ob_eb_data()
    itrac_ob, itrac_eb = get_itrac_ob_eb_data()
    burt_incorrect_steps_per_bug, burt_missing_steps_per_bug = get_burt_data_per_bug()
    itrac_incorrect_steps_per_bug, itrac_missing_steps_per_bug = get_itrac_data_per_bug()

    print(burt_missing_steps, sum(burt_missing_steps))

    print(itrac_missing_steps, sum(itrac_missing_steps))

    print(burt_incorrect_steps, sum(burt_incorrect_steps))
    print(itrac_incorrect_steps, sum(itrac_incorrect_steps))

    print(burt_ob, sum(burt_ob))
    print(burt_eb, sum(burt_eb))
    print(itrac_ob, sum(itrac_ob))
    print(itrac_eb, sum(itrac_eb))

    res_missing = mannwhitneyu(burt_missing_steps, itrac_missing_steps, alternative="less", method="auto")

    print(res_missing)

    res_incorrect = mannwhitneyu(burt_incorrect_steps, itrac_incorrect_steps, alternative="less", method="auto")

    print(res_incorrect)

    res_ob = mannwhitneyu(burt_ob, itrac_ob, alternative="less", method="auto")

    print(res_ob)

    res_eb = mannwhitneyu(burt_eb, itrac_eb, alternative="less", method="auto")

    print(res_eb)
    # copy from the table in the paper
    res_ob_itrac = [0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 2, 1]
    res_ob_burt = [1, 1, 2, 3, 0, 1, 0, 3, 1, 2, 2, 0]
    res_eb_itrac = [0, 1, 1, 1, 1, 0, 0, 1, 0, 2, 0, 1]
    res_eb_burt = [3, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0]

    res_ob_1 = wilcoxon(res_ob_burt, res_ob_itrac, alternative="less", mode="auto")

    print(res_ob_1)

    res_eb_1 = wilcoxon(res_eb_burt, res_eb_itrac, alternative="less", mode="auto")

    print(res_eb_1)

    res_missing_1 = wilcoxon(burt_missing_steps_per_bug, itrac_missing_steps_per_bug, alternative="less", mode="auto")

    print(res_missing_1)

    res_incorrect_1 = wilcoxon(burt_incorrect_steps_per_bug, itrac_incorrect_steps_per_bug, alternative="less",
                               mode="auto")

    print(res_incorrect_1)


if __name__ == '__main__':
    # compute_mann_whiteney()
    '''per bug for missing and incorrect steps'''
    # burt_incorrect_steps_per_bug, burt_missing_steps_per_bug = get_burt_data_per_bug()
    # itrac_incorrect_steps_per_bug, itrac_missing_steps_per_bug = get_itrac_data_per_bug()
    # draw_box_plots(burt_incorrect_steps_per_bug, itrac_incorrect_steps_per_bug,
    #                burt_missing_steps_per_bug, itrac_missing_steps_per_bug)

    '''per bug report for missing and incorrect steps'''
    burt_incorrect_steps, burt_missing_steps = get_burt_data()
    itrac_incorrect_steps, itrac_missing_steps = get_itrac_data()

    draw_box_plots(burt_incorrect_steps, itrac_incorrect_steps,
                   burt_missing_steps, itrac_missing_steps)
