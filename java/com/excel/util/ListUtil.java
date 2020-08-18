import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: newrun-etc
 * @description:
 * @author: liuyanting
 * @create: 2019-09-21 09:52
 **/
public class ListUtil {
    private ListUtil() {
    }

    /**
     * @param : [list: 数组, pageSize: 每页条数]
     * @return : java.util.List<java.util.List<T>>
     * @throws :
     * @Description : 数组分页
     * @author : liuyanting
     * @date : 2019/9/21 9:55
     */
    public static <T> List<List<T>> subList(List<T> list, int pageSize) {
        //切分次数
        int limit = (list.size() + pageSize - 1) / pageSize;
        //获取分割后的集合
        List<List<T>> splitList = Stream.iterate(0, n -> n + 1).limit(limit).parallel().map(a -> list.stream().skip(a * pageSize).limit(pageSize).parallel().collect(Collectors.toList())).collect(Collectors.toList());
        return splitList;
    }

    public static void main(String[] args) {
        String[] arr = "1".split("");
        List<String> list = Arrays.asList(arr);
        List<List<String>> subList = ListUtil.subList(list, 20);
        System.out.println(subList);
    }
}
