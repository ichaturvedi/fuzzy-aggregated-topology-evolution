
# fate
Fuzzy Aggregated Topology Evolution
===
This code implements the model discussed in Fuzzy Agglomerative Topology Evolution. The model is able to determine the optimal neural hyper-parameters for multi-task problems simultaneously. For example, we consider two ECG tasks : Valence (Joy or Sad) and Arousal (Fear or Calm). 

Requirements
---
This code requires Matlab for Fuzzy preprocessing and Jave for Genetic Programming.

Preprocessing
---
The training data is a csv file of features followed by class label. Example is in folder fuzzy/dataset.

Training
---

- Format both tasks as nn input : nn_input.m
- Create population for task 1 : ecg_nn_task1.m
- Create population for task 2 : ecg_nn_task2.m
- Transform both tasks using Fuzzy logic : fuzzy.m
- Run MFGP on the transformed data : java MFGP/Main.java


Testing
---
To test a new sample you have to solve the predicted optimal GP tree
