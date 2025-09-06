# SOLUTION.md

## UrlCount – Hadoop MapReduce Project

### 1. Solution Overview
For this lab, I modified the standard **WordCount** Hadoop program into a new program called **UrlCount**.  
Instead of counting words, the Mapper extracts all URLs that appear in the format `href="..."` from the input Wikipedia articles.  
The Reducer sums the occurrences of each URL and only outputs URLs that appear **more than 5 times**.  

I implemented this in **Java MapReduce**, compiled it into `UrlCount.jar`, and tested it both locally (CSEL environment) and on **Google Cloud Dataproc**.

---

### 2. Software / Dependencies
  
- **Hadoop 3.3.4** (preconfigured in CSEL environment)  
- **Google Cloud Dataproc** (clusters with 2 and 4 workers)  
- **GitHub** for version control and syncing code to Dataproc  

---

### 3. Methodology
1. Started with the provided `WordCount1.java`.  
2. Modified the Mapper to extract URLs using Java regex:  
   ```
   java
   private static final Pattern HREF_PATTERN = Pattern.compile("href=\\\"([^\\\"]*)\\\"");
   ```
   
3. Implemented a Reducer that only outputs if the count > 5.
4. Compiled with:
```
make UrlCount.jar
```
5. Ran locally in CSEL with:
```
hadoop jar UrlCount.jar UrlCount input output-urlcount

```
6. Verified output with:
```
hdfs dfs -cat output-urlcount/part-r-00000 | head
```
---

### 4. Expected Output

There should be four URLs with counts greater than 5, matching the assignment description:
```
/wiki/Doi_(identifier)  16
/wiki/ISBN_(identifier) 18
/wiki/S2CID_(identifier)        12
mw-data:TemplateStyles:r1238218222      121
mw-data:TemplateStyles:r1295599781      33
```
---

## Interpretation

1. **2-node cluster was faster** for this dataset.  
   - Lower reduce time (17s vs 98s).  
   - Fewer launched reducers = less coordination overhead.  

2. **4-node cluster added overhead**:  
   - Extra reducers increased shuffle, merge, and communication costs.  
   - Good for **very large datasets**, but not efficient for small input sizes like ~4236 records.  

3. **Same final output**:  
   - Number of reduce output records = **7**.  
   - Both clusters computed identical counts, showing Hadoop’s determinism regardless of cluster size.
  
## Resources used

- Resources used: course materials and readme provided, Hadoop documentation, and the official Google Cloud Dataproc documentation.  


---

## Conclusion

- **For small jobs** (few MBs of data), a **smaller cluster (2 nodes)** is more efficient because less scheduling and communication overhead.  
- **For large-scale jobs** (GBs–TBs of data), a **larger cluster (4+ nodes)** would outperform because tasks can run in parallel and balance across nodes.
