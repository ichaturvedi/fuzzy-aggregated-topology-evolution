errorall = zeros(100,10);
for p=1:100
   
    filename = sprintf('population1/data%d.txt',p);
    task1 = load(filename);
    
    filename = sprintf('population1/labels%d.csv',p);
    label1 = load(filename);
    
    filename = sprintf('population2/data%d.txt',p);
    task2 = load(filename);
    
    filename = sprintf('population2/labels%d.csv',p);
    label2 = load(filename);
    
    data = [task1; task2];
    label = [label1; label2];
    
    data_train = [data label];
    
    numMFs = 2;                     % number of membership functions
    inmftype = 'gaussmf';           % membership function type for input
    outmftype = 'linear';           % membership function type for output
    n_epochs = 10;                % number of epochs
    
    % generate Sugeno-type FIS structure from data using grid partition
    a = genfis1(data_train, numMFs, inmftype, outmftype)

    % start training
    [fis, error] = anfis(data_train, a, n_epochs);
    errorall(p,:) = error;
    
    % to check with training dataset
    data_train_chk = data_train(:,1:end-1);

    % ANFIS classifying training
    output_train = evalfis(data_train_chk, fis);
    
    if p == 1
        task1b =  output_train(1:size(task1,1));
    else
        task1b = [task1b output_train(1:size(task1,1))];
    end
    
    if p == 1
        task2b =  output_train(size(task1,1)+1:end);
    else
        task2b = [task2b output_train(size(task1,1)+1:end)];
    end
    
end

task1b = [task1b label1];
task2b = [task2b label2];

dlmwrite('task1.csv',task1b(1:size(task2b,1),:));
dlmwrite('task2.csv',task2b);

