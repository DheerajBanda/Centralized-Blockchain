# Centralized-Blockchain

To execute the code in checkpoint1:
1. move to Project-v1 folder in Checkpoint-1.
2. execute the command "mvn clean install"
3. execute the command "java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar <argument-1>"
4. example of running a wallet create command "java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar wallet create"

To run checkpoint2:
1. move to Checkpoint-2 folder
2. Download Project-v1.zip
3. unzip the folder Project-v1.zip

Monitor, Metronome, pool, Blockchain, validator.
Start the servers in the above-provided order.


**Checkpoint-3-Final is the final folder of the project. (README file is also provided in the folder Checkpoint-3-Final/Project-v1)(same as below)**

Makefile commands (enter into the folder that contains the Makefile Checkpoint-3-Final/Project-v1):

    1. make init
        installs the required software to run the program.
    2. make clean
        clears the compiled code and gets everything ready for recompile
    3. make all
        compiles the code (enter this command after downloading the software using make init).

Commands to run the components(enter into Checkpoint-3-Final/Project-v1):

    the command to run the components is - java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar <arguments>
    
    1. java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar wallet create 
    2. java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar monitor
    3. java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar metronome
    4. java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar pool
    5. java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar blockchain
    6. java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar validator

*NOTE: the order of starting the servers (IMPORTANT)
        monitor -> metronome -> pool -> blockchain -> validators*
        
*NOTE: dsc-config.yaml also contains the IP and port of the monitor.*

Commands for evaluation(enter into Checkpoint-3-Final/Project-v1):

    1. latency
        java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar wallet latency <numOfTransactions> <amount> <recipientPublicAddress>\\
        ex: java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar wallet latency 128 0.1 AKjuV87n82B
    2. throughput
        java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar wallet throughput <numOfTransactions> <amount> <recipientPublicAddress>
        ex: java -jar target/DataSysCoinv1-0.0.1-SNAPSHOT-jar-with-dependencies.jar wallet throughput 128000 0.0001 AKjuV87n82B

*NOTE: make sure the coins are 0.1 for latency as the initial amount is 100 in a wallet. (if the amount is more than the balance it will not throw an error)*

*NOTE: Download the project report for a clear view of screenshots of the evaluation.*


