#!/usr/bin/env bash
# Note that this script is written specifically for Ubuntu 16.04
# If you use a Ubuntu 14.04 VM, please switch to an Ubuntu 16.04 instance as 14.04 do not have the correct version of Python(>=3.5)
sudo apt-get update
sudo apt-get install git
sudo apt-get install unzip
sudo apt-get install python3-pip
sudo apt-get install make
# install jdk 1.8
sudo apt-get install default-jdk
# Get Stanford CoreNLP
cd ../
rm -if stanford-corenlp-full-2017-06-09.zip
rm -rf stanford-corenlp-full-2017-06-09
wget http://nlp.stanford.edu/software/stanford-corenlp-full-2017-06-09.zip
unzip stanford-corenlp-full-2017-06-09.zip
cd IterativeSetExpansion
# Get dependencies
pip3 install -r requirements.txt
# run command with nohup
nohup make run &

