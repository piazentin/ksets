Java K-sets implement levels KO to KIII of the Freeman K-set Models was built and is built. 
The software developed in Java allows for easy integration with MATLAB and other environments. See the following Matlab example: 

    % Create KIII
    K = main.ksets.kernel.KIII(3, 1, 1);
    K.switchLayerTraining([true false false]);
    K.setOutputLayer(0);
    K.initialize;
    
    % train with dataset
    K.train(dataset(randperm(size(dataset, 1)),:));
    
    % run for dataset
    kresult = K.run(dataset);

