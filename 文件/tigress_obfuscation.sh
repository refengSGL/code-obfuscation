#!/bin/bash

# 1st Argument is the name of the C file (without extension) that should be obfuscated
# 2nd Argument is the function you want to protect
# 3rd Argument is type of obfuscation

if [ ${3} -eq 1 ];
then
    # Flatten
    tigress --Environment=x86_64:Linux:Gcc:4.6 --Transform=Flatten --Functions=${2} --out=${1}_flatten.c ${1}.c
elif [ ${3} -eq 2 ];
then
    # EncodeArithmetic
    tigress --Environment=x86_64:Linux:Gcc:4.6 --Transform=EncodeArithmetic --Functions=${2} --out=${1}_encodeArithmetic.c ${1}.c
elif [ ${3} -eq 3 ];
then
    #4th argument is the "InitOpaqueStructs",such as list,array,input,env,*
    #5th argument is "InitOpaqueCount"
    #6th argument is "AddOpaqueKinds",such as call,bug,true
    #7th argument is "AddOpaqueCount"
    #8th argument is "AddOpaqueStructs",such as list,array,input,env,*
    tigress --Environment=x86_64:Linux:Gcc:4.6 --Transform=InitOpaque --InitOpaqueStructs=${4} --InitOpaqueCount=${5} --Functions=${2} --Transform=AddOpaque --AddOpaqueKinds=${6} --AddOpaqueCount=${7} --AddOpaqueStructs=${8} --Functions=${2} --out=${1}_addOpaque.c ${1}.c
fi


#for example

#tigress --Environment=x86_64:Linux:Gcc:4.6 --Transform=Flatten --Functions=main --out=test_flatten.c test.c

#tigress --Environment=x86_64:Linux:Gcc:4.6 --Transform=EncodeArithmetic --Functions=main --out=test_encodeArithmetic.c test.c

#tigress --Environment=x86_64:Linux:Gcc:4.6 --Transform=InitOpaque --InitOpaqueStructs=list --InitOpaqueCount=1 --Functions=main --Transform=AddOpaque --AddOpaqueKinds=true --AddOpaqueCount=1 --AddOpaqueStructs=list --Functions=main --out=test_addOpaque.c test.c



