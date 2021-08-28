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
import pandas as pd

category_names = ['1' ]
results = {
    'Strongly disagree': [26],
    'Disagree': [0],
    'Neither agree nor disagree': [19],
    'Agree': [33],
    'Strongly agree': [40],
}


def survey(results, category_names):
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
    category_colors = plt.get_cmap('RdYlGn')(
        np.linspace(0.05, 0.85, ))
    df = pd.DataFrame(results, index=category_names)
    ax = df.plot.barh(stacked=True, cmap='Set3', figsize=(8, 2))
    print(ax.containers)
    for c in ax.containers:
        print(c)
        for v in c:
            print(v)
        # format the number of decimal places and replace 0 with an empty string
        labels = [f'{w:.0f}' if (w := v.get_width()) > 0 else '' for v in c]
        print(labels)

        ax.bar_label(c, labels=labels, label_type='center')
    ax.legend(ncol=5, bbox_to_anchor=(0, 1),
              loc='lower left', fontsize='small')


survey(results, category_names)
plt.show()

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
