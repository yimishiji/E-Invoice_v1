import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018/1/25.
 */
public class sad {

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String birthday=simpleDateFormat.format("2017-1-1");
        System.out.println(birthday);
    }
}
