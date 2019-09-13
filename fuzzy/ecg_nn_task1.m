nclass = 2;  
traind = 1; % 1 or train , 2 for test
trfunctionarr = {'trainbr','trainbfg','trainlm'};
mkdir('population1');
check = 0 ;

for p=1:100

%parameters
neu = randi(10);
epochs = randi(50);
learning_rate = rand(1,1);
regularization = rand(1,1);
trfunction = randi(3);

paras = [neu epochs learning_rate regularization trfunction];
if check == 1
  filename = sprintf('population2/paras%d.txt',p);
  paras2 = load(filename);
  neu = paras2(1);
  epochs = paras2(2);
  learning_rate = paras2(3);
  regularization = paras2(4);
  trfunction = paras2(5);
  paras = paras2;
end

filename = sprintf('population1/paras%d.txt',p);
dlmwrite(filename, paras);

for k=1:1

filename = sprintf('set1_train_rnn%d',k);
train1 = importdata(filename);
filename = sprintf('set1_val_rnn%d',k);
val = importdata(filename);
filename = sprintf('set1_test_rnn%d',k);
test = importdata(filename);

train2 = train1(:,1);
for m=2:size(train1,2)
    if any(isnan(train1(:,m))) == 1       
        1;
    else
        train2 = [train2 train1(:,m)];
    end   
end

test2 = test(:,1);
for m=2:size(test,2)   
    if any(isnan(test(:,m))) == 1       
        1;
    else
        test2 = [test2 test(:,m)];
    end   
end

val2 = val(:,1);
for m=2:size(val,2)   
    if any(isnan(val(:,m))) == 1       
       1;
    else
       val2 = [val2 val(:,m)];
    end   
end

train1 = train2;
val = val2;
test = test2;

[n1 n2]=size(train1);
[n3 n4a]=size(val);
[n5 n6]=size(test);
train4 = [train1 val test];
n4 = n2+n4a;

[n7 n8] = size(train4);
X = train4(1:end-1,:);
X = X(1:500,:);
T = train4(end,:);

%dlmwrite('data_rnny.txt',T);

filename = sprintf('population1/labels%d.csv',p);
dlmwrite(filename,T');


T2=transformtarget(T,nclass);
T2 = T2';
T = T2;

if traind == 2
  filename = sprintf('population1/train_rnn_%d',p);
  net1=load(filename);
  net = net1.net;
end

if traind == 1
  %net = layrecnet(1:2,neu);
  net = feedforwardnet(neu);
  net = configure(net,X,T);
end
net.trainFcn = trfunctionarr{trfunction};
%net.trainFcn = 'trainbfg';
net.trainParam.epochs = epochs;
net.trainParam.lr = learning_rate;
%net.trainParam.showWindow = false;
%net.trainParam.showWindow=0;
net.performParam.regularization = regularization;
net.divideFcn = 'divideind';
net.performFcn = 'mse';
net.divideParam.trainInd = 1:n2;
net.divideParam.valInd   = n2+1:n4;
net.divideParam.testInd  = n4+1:size(train4,2);

filename = sprintf('population1/train_test_val%d.txt',p);
dlmwrite(filename,[n2, n4, size(train4,2)]);

if traind == 1
[net,tr] = train(net,X,T);
end

ncase=size(X,2);
Y = net(X);

%features
newy = train4(end,:);
newx = [T(:,1:n4)'*net.LW{2,1};Y(:,n4+1:end)'*net.LW{2,1}];
newx = newx';
newdata = [newx; newy];
filename = sprintf('population1/data%d.txt',p);
dlmwrite(filename,newdata);

% output node
%newx = Y'*net.LW{2,1};
%filename = sprintf('population1b/data%d.txt',p);
%dlmwrite(filename,Y');

if traind == 1
filename = sprintf('population1/train_rnn_%d',p);
save (filename, 'net') ;
end

[num idx] = max(T(:,n4+1:end));
[num2 idx2] = max(Y(:,n4+1:end));
%dlmwrite('pred_label.txt',idx2'-1);

if traind > 0
cm = confusionmat(idx,idx2);
for x=1:nclass

tp = cm(x,x);
tn = cm(1,1);
for y=2:nclass
tn = tn+cm(y,y);
end
tn = tn-cm(x,x);

fp = sum(cm(:, x))-cm(x, x);
fn = sum(cm(x, :), 2)-cm(x, x);
pre(x)=tp/(tp+fp+0.01);
rec(x)=tp/(tp+fn+0.01);
%fmea(x) = 2*pre(x)*rec(x)/(pre(x)+rec(x)+0.01);
fmea(x) = (tp+tn)/(tp+fp+tn+fn);

end


classfmea=fmea;
%fmea = sum(fmea)/nclass;

fmea
allfmea=fmea;

allfmea

%dlmwrite('allfmea.txt',allfmea);
%dlmwrite('classfmea.txt',classfmea);

end

end


end