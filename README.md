
Multi-task Genetic Programming
===
This code implements the model discussed in the paper _Fuzzy Aggregated Topology Evolution_. The model is able to determine the optimal neural hyper-parameters for multi-task problems simultaneously. Genetic Program is used to represent the solution for each task. For example, we consider two ECG tasks : Valence (Joy or Sad) and Arousal (Fear or Calm). 

Requirements
---
This code is based on NEAT code found here : https://github.com/CodeReclaimers/neat-python

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
