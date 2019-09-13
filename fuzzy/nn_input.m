
data=load('dataset/task1.txt');

data2 = data(randperm(size(data,1)),:);
data = data2;

k=10;
[test train] = kfolds(data,k);

for i=1:1
   
    [n1 n2]=size(train{i});
    vali = floor(0.7*n1);
    
    set_train_rnn = train{i}(1:vali,:);
    set_val_rnn = train{i}(vali+1:end,:);
    
    filename = sprintf('set1_train_rnn%d',i);
    dlmwrite(filename,set_train_rnn');
    
    filename = sprintf('set1_val_rnn%d',i);
    dlmwrite(filename,set_val_rnn');
    
    filename = sprintf('set1_test_rnn%d',i);
    dlmwrite(filename,test{i}');
    
end

clear

data=load('dataset/task2.txt');

data2 = data(randperm(size(data,1)),:);
data = data2;

k=10;
[test train] = kfolds(data,k);

for i=1:1
   
    [n1 n2]=size(train{i});
    vali = floor(0.7*n1);
    
    set_train_rnn = train{i}(1:vali,:);
    set_val_rnn = train{i}(vali+1:end,:);
    
    filename = sprintf('set2_train_rnn%d',i);
    dlmwrite(filename,set_train_rnn');
    
    filename = sprintf('set2_val_rnn%d',i);
    dlmwrite(filename,set_val_rnn');
    
    filename = sprintf('set2_test_rnn%d',i);
    dlmwrite(filename,test{i}');
    
end
