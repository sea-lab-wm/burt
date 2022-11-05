
import pandas as pd
import numpy as np

def compute_ranks(ground_truth_states_str, retrieved_states_str):
    
    ground_truth_states = list(map(lambda x: x.strip(), ground_truth_states_str[1:-1].split(",")))
    #ground_truth_states = list(map(lambda x: int(x), ground_truth_states))

    retrieved_states = list(map(lambda x: x.strip(), retrieved_states_str[1:-1].split(",")))
    #retrieved_states = list(map(lambda x: int(x), retrieved_states))

    #print(ground_truth_states, retrieved_states)

    ranks = []
    for state in ground_truth_states:
        if state in retrieved_states:
            ranks.append(retrieved_states.index(state) + 1)

    return ranks

def first_rank(ranks):
    return None if not ranks else min(ranks)

def reciprocal_rank(first_rank):
    return 0 if pd.isna(first_rank) else 1 / first_rank

def mrr(data_frame):
    print(data_frame)
    return data_frame["recip_rank"].mean()

def average_precision(ground_truth_states_str, ranks):

    ground_truth_states = list(map(lambda x: x.strip(), ground_truth_states_str[1:-1].split(",")))

    precision_sum = 0.0
    num_tp = 0
    for rank in ranks:
        num_tp += 1
        precision_atk = num_tp / rank
        precision_sum += precision_atk
    return precision_sum / len(ground_truth_states)

if __name__ == "__main__":

    retrieval_data = pd.read_csv("retrieval_data_test.csv")

    retrieval_data["ranks"] = retrieval_data.apply(lambda x: compute_ranks(x.ground_truth_states, x.retrieved_states), axis=1)
    retrieval_data["first_rank"] = retrieval_data.apply(lambda x: first_rank(x.ranks), axis=1)
    retrieval_data["recip_rank"] = retrieval_data.apply(lambda x:  reciprocal_rank(x.first_rank), axis=1)
    retrieval_data["avg_prec"] = retrieval_data.apply(lambda x:  average_precision(x.ground_truth_states, x.ranks), axis=1)

    #pd.set_option('display.max_columns', None)
    print(retrieval_data)

    retrieval_data.to_csv("retrieval_data_test_indiv_metrics.csv")

    # ----------

    # results_summary = pd.DataFrame({
    #     "config" : [],
    #     "num_queries" : [],
    #     "mrr" : [],
    #     "map" : [],
    #     "hit1" : [],
    #     "hit2" : []
    # })

    # print(results_summary)

    results_summary = retrieval_data.groupby('config').apply(
        lambda x: pd.Series({
            "num_bugs" : len(x.config),
            "num_retr" : len(x[pd.notna(x.first_rank)]),
            "perc_retr" : len(x[pd.notna(x.first_rank)])  / len(x.config),
            "avg_rank" : np.nanmean(x.first_rank),
            "mrr" : np.average(x.recip_rank),
            "map" : np.average(x.avg_prec),
            "h1" : len(x[x.first_rank <= 1]) / len(x.config),
            "h3" : len(x[x.first_rank <= 3]) / len(x.config),
            "h5" : len(x[x.first_rank <= 5]) / len(x.config),
            "h7" : len(x[x.first_rank <= 7]) / len(x.config),
            "h10" : len(x[x.first_rank <= 10]) / len(x.config),
            })
    )
    print(results_summary)
    results_summary.to_csv("results.csv")