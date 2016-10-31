import java.util.*;
import java.util.stream.*;
import java.nio.file.*;


public class CourseBlock {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println(
				"Usage: java CourseBlock blockSize blockOccurs enrollFile");
			return;
		}
		long minBlockSize = Long.parseLong(args[0]);
		long minBlockOccurs = Long.parseLong(args[1]);

		Stream<String> enrollStream;

		// data stream from sample file
		enrollStream = Files.lines(Paths.get(args[2]));

		// data stream from db
		// SELECT LISTAGG(course_combo, ' ') WITHIN GROUP (ORDER BY course_combo) 
  		// FROM courses
  		// WHERE year='2015' AND term='9'
  		// GROUP BY ruid

		// data stream from string array
		// enrollStream = Arrays.stream(
		// 	"1 2 3", "2 3 4 5", "3 4 5", "4 5 6"
		// );

		// collect course combinations from all students
		List<String> blocks = new ArrayList<String>();
		enrollStream.forEach(e->{
			blocks.addAll(
				combination( // generate all combinations
					new ArrayList<String>(Arrays.asList(e.split(" ")))
				).stream()
				.filter(x->(x.size()>=minBlockSize)) // block size
				.map(x->{ // map course list to course combination string
					Collections.sort(x); // same string for same set
					return Arrays.toString(x.toArray(new String[0])); 
				}).collect(Collectors.toList())
			);			
		});

		// count for each course block
		blocks.stream()
		.collect( // group by course combination string
			Collectors.groupingBy((s->s), Collectors.counting())
		).entrySet().stream()
		.filter(x->x.getValue()>=minBlockOccurs) // course block occurs
		.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
		.forEach(x->System.out.println(x.getValue() + " : " + x.getKey()));
	}

	public static List<List<String>> combination(List<String> courseList) {
		List<List<String>> result = new ArrayList<List<String>>();
		if (courseList.size() > 0) {
			String me = courseList.remove(0);
			result.add(new ArrayList<String>(Arrays.asList(me)));
			List<List<String>> others = combination(courseList);
			result.addAll(others);
			result.addAll(
				others.stream().map(x -> {
					List<String> w = new ArrayList<String>(x); 
					w.add(me); 
					return w;
				}).collect(Collectors.toList())
			);
		}
		return result;
	}

	public static void printList(List l) {
		l.stream().forEach(x -> {
			if (x instanceof List)
				printList((List)x);
			else
				System.out.print(x+" ");
		});
		System.out.println();
	}
}