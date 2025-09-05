import java.io.IOException;
import java.util.StringTokenizer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class UrlCount {

  public static class UrlMapper extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text urlText = new Text();

    // public void map(Object key, Text value, Context context
    //                 ) throws IOException, InterruptedException {
    //   StringTokenizer itr = new StringTokenizer(value.toString());
    //   while (itr.hasMoreTokens()) {
    //     word.set(itr.nextToken());
    //     context.write(word, one);
        private static final Pattern HREF_PATTERN =
            Pattern.compile("href=\\\"([^\\\"]*)\\\"");

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String line = value.toString();
        Matcher matcher = HREF_PATTERN.matcher(line);

        while (matcher.find()) {
                urlText.set(matcher.group(1));
                context.write(urlText, one);


      }
    }
  }

  //   public static class IntSumCombiner
  //      extends Reducer<Text,IntWritable,Text,IntWritable> { 
  //   private IntWritable result = new IntWritable();

  //   public void reduce(Text key, Iterable<IntWritable> values,
  //                      Context context
  //                      ) throws IOException, InterruptedException {
  //     int sum = 0;
  //     for (IntWritable val : values) {
  //       sum += val.get();
  //     }
  //     result.set(sum);
  //     context.write(key, result);
  //   }
  // }

    
  // public static class IntSumReducer
  //      extends Reducer<Text,IntWritable,Text,IntWritable> { 
  //   private IntWritable result = new IntWritable();

  //   public void reduce(Text key, Iterable<IntWritable> values,
  //                      Context context
  //                      ) throws IOException, InterruptedException {
  //     int sum = 0;
  //     for (IntWritable val : values) {
  //       sum += val.get();
  //     }
  //     result.set(sum);
  //       if ( sum > 5) {
  //     context.write(key, result);
  //       }
  //   }
  // }

    public static class UrlReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            if (sum > 5) {
                result.set(sum);
                context.write(key, result);
            }
        }
    }



  public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "url count");
        job.setJarByClass(UrlCount.class);
        job.setMapperClass(UrlMapper.class);
        job.setCombinerClass(UrlReducer.class);
        job.setReducerClass(UrlReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

  }
}
