# InformaticaTestCode

The code sorts big text file and generates an `final_result` file that contains unique words in ascending order.

1. To launch the file execute the `Launcher.java`
2. `Launcher.java` will ask for input file name. Please enter full path. Example `c:\Sample\my_input_text.txt`.
3. Finally, it will generate a file called `final_result`. It will contain the unique words in ascending order, used `String` `compareTo` method to sort.

# Architecture

1. We read input files in chunks and feed each chunk to individual threads. 
2. Each thread sorts its respective chunk and generates a sorted file.
3. Finally, we feed group of sorted files to individual threads called reducers. 
4. Reducers merges the sorted file using K-way merge sort.

# Avoid out of memory error

1. To avoid out of memory error, tune `MAX_LINE_READ_COUNT`. It is the size of the chunk of lines read from the input file. 
2. If getting out of memory error, reduce the size of the MAX_LINE_READ_COUNT.
	`public static final int MAX_LINE_READ_COUNT = 10000;`
3. `MAX_MEMORY_LINE_CONDITION` is also used to avoid out of memory error. Its limits the maximum number of lines processed by all threads combined. 
4. To avoid out of memory error keep it small.
5. Also, respect the following condition, while tuning them: `MAX_MEMORY_LINE_CONDITION > MAX_LINE_READ_COUNT`
	`public static final int MAX_MEMORY_LINE_CONDITION = 100000;`
  
6. Both `MAX_LINE_READ_COUNT` and `MAX_MEMORY_LINE_CONDITION` resides in `Constant.java`.
