package util;

/**
 * Created by Administrator on 2018/3/6.
 */
public class Sending implements Runnable {

    private String to;    //收件人
    private String subject;    //主题
    private String content;    //内容
    private String fileStr;    //附件路径

    public Sending(String to, String subject, String content, String fileStr) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.fileStr = fileStr;
    }

    @Override
    public void run() {
        SendEmailUtil sendEmail = new SendEmailUtil(to, subject, content, fileStr);
        sendEmail.send();
    }
}

