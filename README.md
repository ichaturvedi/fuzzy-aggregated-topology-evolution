# fate
Fuzzy Aggregated Topology Evolution
% Task1 Valence
% Task2 Arousal

% format both tasks as nn input
nn_input

% create population for task 1
ecg_nn_task1

% create population for task 2
ecg_nn_task2

% transform both tasks using Fuzzy logic
fuzzy

% run MFGP on the transformed data
cd MFGP
java Main
