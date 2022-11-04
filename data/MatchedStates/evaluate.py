import csv


def get_num_tp(top_k_devs, devs_ground_truth):
    return len(set(top_k_devs).intersection(set(devs_ground_truth)))


def get_average_precision(devs_ground_truth, indices):
    precision_sum = 0.0
    num_tp = 0
    for index in indices:
        k = index + 1
        num_tp += 1
        precision_atk = num_tp / k
        precision_sum += precision_atk
    return precision_sum / len(devs_ground_truth)


def compute_metrics(ground_truth, prediction_list):
    indices = []
    # print("bug_id: ", bug_report["bug_id"], devs_prediction_list)
    # print("bug_id: ", bug_report["bug_id"], "ground_truth: ", devs_ground_truth)
    for i in range(len(prediction_list)):
        if prediction_list[i] in ground_truth:
            indices.append(i)
    first_rank = 0
    rr = 0.0
    ap = 0.0
    if len(indices) > 0:
        first_rank = indices[0] + 1
        rr = 1.0 / first_rank
        ap = get_average_precision(ground_truth, indices)
    kmax = 5
    hits = [0] * kmax
    precisions = [0.0] * kmax
    recalls = [0.0] * kmax
    for k in range(1, kmax + 1):
        if first_rank != 0 and first_rank <= k:
            hits[k - 1] = 1
        else:
            hits[k - 1] = 0
        if len(prediction_list) < k:
            top_k_devs = prediction_list
        else:
            top_k_devs = prediction_list[0:k]
        num_tp = get_num_tp(top_k_devs, ground_truth)
        precisions[k - 1] = num_tp / k
        recalls[k - 1] = num_tp / len(ground_truth)

    return first_rank, rr, ap, hits, precisions, recalls


def evaluate_new(csv_file, outoput_file):
    csvFile = open(csv_file, "r")
    reader = csv.reader(csvFile)

    with open(outoput_file, "w",
              newline='') as csv_file:
        writer = csv.writer(csv_file, delimiter=';')
        writer.writerow(["bug_id", "first_rank", "recip_rank", "avg_precision", "h@1", "p@1", "r@1",
                         "h@2", "p@2", "r@2", "h@3", "p@3", "r@3", "h@4", "p@4", "r@4", "h@5", "p@5", "r@5"])

        for item in reader:
            if reader.line_num == 1:
                continue
            bug_id = item[0]
            matchedStates_string = item[2][1:-1]
            matchedStates_list = matchedStates_string.split(",")
            matchedStates_list = list(map(lambda x: int(x), matchedStates_list))
            if len(item[3]) > 2:
                rankedPredictedStates_string = item[3][1:-1]
                rankedPredictedStates_list = rankedPredictedStates_string.split(",")

                rankedPredictedStates_list = list(map(lambda x: int(x), rankedPredictedStates_list))
            else:
                rankedPredictedStates_list = []

            first_rank, rr, ap, hits, precisions, recalls = compute_metrics(matchedStates_list, rankedPredictedStates_list)
            eval_result = [bug_id, first_rank, rr, ap]
            for i in range(len(hits)):
                eval_result.extend([hits[i], precisions[i], recalls[i]])
            writer.writerow(eval_result)
        csvFile.close()


if __name__ == "__main__":
    evaluate_new("matched_states_ob.csv", "evaluation_ob_new.csv")
    evaluate_new("matched_states_s2r.csv", "evaluation_s2r_new.csv")
