# Centralized-Blockchain

**Makefile commands (enter into the folder that contains the Makefile db-coin/):**

    1. make init
        installs the required software to run the program.
    2. make clean
        clears the compiled code and gets everything ready for recompile
    3. make all
        compiles the code (enter this command after downloading the software using make init).

**Commands to run the components(enter into db-coin/):**

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


