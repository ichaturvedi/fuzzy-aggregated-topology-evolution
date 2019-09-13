function [test, train] = kfolds(data, k)

  n = size(data,1);

  test{k,1} = [];
  train{k,1} = [];

  chunk = floor(n/k);

  test{1} = data(1:chunk,:);
  train{1} = data(chunk+1:end,:);

  for f = 2:k
      test{f} = data((f-1)*chunk+1:(f)*chunk,:);
      train{f} = [data(1:(f-1)*chunk,:); data(f*chunk+1:end, :)];
  end
end