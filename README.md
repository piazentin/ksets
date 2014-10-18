Java implementation for Freeman K-set models
Copyright (C) 2014  Denis Renato de Moraes Piazentin
E-mail for contact: denis at piazentin.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

Java K-sets version 1.0, Copyright (C) 2014 Denis Renato de Moraes Piazentin
Java K-sets comes with ABSOLUTELY NO WARRANTY;

--------------------------------------------------------------

[![DOI](https://zenodo.org/badge/6681/denisrmp/ksets.png)](http://dx.doi.org/10.5281/zenodo.12288)

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

