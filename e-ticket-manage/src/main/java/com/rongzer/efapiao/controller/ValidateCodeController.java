package com.rongzer.efapiao.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rongzer.ecservice.common.base.ECBaseBean;

@Controller
@RequestMapping
public class ValidateCodeController extends ECBaseBean{
	
	Logger log = Logger.getLogger(ValidateCodeController.class);
	public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";//放到session中的key
    private static Random random = new Random();
    private static String randString = "1234567890";//随机产生的字符串
    
    private int width = 150;//图片宽
    private int height = 60;//图片高
    private int lineSize = 5;//干扰线数量
    private int stringNum = 4;//随机产生字符数量

	@RequestMapping(value="/**/getRandcode" , method=RequestMethod.GET)
	public @ResponseBody  void getRandcode(HttpServletRequest request,
            HttpServletResponse response){
		 //BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		Color[] colors = new Color[5];
		Color[] colorSpaces = new Color[] { Color.WHITE, Color.CYAN,
				Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
				Color.PINK, Color.YELLOW };
		float[] fractions = new float[colors.length];
		for(int i = 0; i < colors.length; i++){
			colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
			fractions[i] = rand.nextFloat();
		}
		Arrays.sort(fractions);
		
		g2.setColor(Color.WHITE);// 设置边框色
		g2.fillRect(0, 0, width, height);
		
		Color c = getRandColor(200, 250);
		g2.setColor(Color.WHITE);// 设置背景色
		g2.fillRect(0, 2, width, height-4);
		
		//绘制干扰线
		Random random = new Random();
		g2.setColor(getRandColor(160, 200));// 设置线条的颜色
		for (int i = 0; i < lineSize; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int xl = random.nextInt(6) + 1;
			int yl = random.nextInt(12) + 1;
			g2.drawLine(0, y+yl,x + xl+40 , y);
		}
		
		// 添加噪点
		float yawpRate = 0.03f;// 噪声率
		int area = (int) (yawpRate * width * height);
		for (int i = 0; i < area; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int rgb = getRandomIntColor();
			image.setRGB(x, y, rgb);
		}
		
		//shear(g2, width, height, c);// 使图片扭曲
		
		g2.setColor(getRandColor(100, 160));
		int fontSize = height-4;
		Font font = new Font("Algerian", Font.ITALIC, fontSize);
		g2.setFont(font);
		
		String generateVerifyCode = generateVerifyCode(stringNum);
		char[] chars = generateVerifyCode.toCharArray();
		for(int i = 0; i < stringNum; i++){
			AffineTransform affine = new AffineTransform();
			affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (width / stringNum) * i + fontSize/2, height/2);
			g2.setTransform(affine);
			g2.drawChars(chars, i, 1, ((width-10) / stringNum) * i + 5, height/2 + fontSize/2 - 10);
		}
       
        String CookieValue = (String)request.getAttribute("COOKIE_VALUE");
        String cookieId = CookieValue+"_RANDONCODEKEY";
        try 
        {	//将验证码内容放入memcached中
			this.saveCache(cookieId, generateVerifyCode);
			g2.dispose();
            ImageIO.write(image, "JPEG", response.getOutputStream());//将内存中的图片通过流动形式输出到客户端
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	g2 = null;
        	image = null;
        }
	}
	
	/**
	 * 使用系统默认字符源生成验证码
	 * @param verifySize	验证码长度
	 * @return
	 */
	public static String generateVerifyCode(int verifySize){
		return generateVerifyCode(verifySize, randString);
	}
	
	/**
	 * 使用指定源生成验证码
	 * @param verifySize	验证码长度
	 * @param sources	验证码字符源
	 * @return
	 */
	public static String generateVerifyCode(int verifySize, String sources){
		if(sources == null || sources.length() == 0){
			sources = randString;
		}
		int codesLen = sources.length();
		Random rand = new Random(System.currentTimeMillis());
		StringBuilder verifyCode = new StringBuilder(verifySize);
		for(int i = 0; i < verifySize; i++){
			verifyCode.append(sources.charAt(rand.nextInt(codesLen-1)));
		}
		return verifyCode.toString();
	}
	
	private static int getRandomIntColor() {
		int[] rgb = getRandomRgb();
		int color = 0;
		for (int c : rgb) {
			color = color << 8;
			color = color | c;
		}
		return color;
	}
	
	private static int[] getRandomRgb() {
		int[] rgb = new int[3];
		for (int i = 0; i < 3; i++) {
			rgb[i] = random.nextInt(255);
		}
		return rgb;
	}
	
	private static void shear(Graphics g, int w1, int h1, Color color) {
		shearX(g, w1, h1, color);
		shearY(g, w1, h1, color);
	}
	
	private static void shearX(Graphics g, int w1, int h1, Color color) {

		int period = random.nextInt(2);

		boolean borderGap = true;
		int frames = 1;
		int phase = random.nextInt(2);

		for (int i = 0; i < h1; i++) {
			double d = (double) (period >> 1)
					* Math.sin((double) i / (double) period
							+ (6.2831853071795862D * (double) phase)
							/ (double) frames);
			g.copyArea(0, i, w1, 1, (int) d, 0);
			if (borderGap) {
				g.setColor(color);
				g.drawLine((int) d, i, 0, i);
				g.drawLine((int) d + w1, i, w1, i);
			}
		}

	}

	private static void shearY(Graphics g, int w1, int h1, Color color) {
		int period = random.nextInt(40) + 10; // 50;
		boolean borderGap = true;
		int frames = 20;
		int phase = 7;
		for (int i = 0; i < w1; i++) {
			double d = (double) (period >> 1)
					* Math.sin((double) i / (double) period
							+ (6.2831853071795862D * (double) phase)
							/ (double) frames);
			g.copyArea(i, 0, 1, h1, 0, (int) d);
			if (borderGap) {
				g.setColor(color);
				g.drawLine(i, (int) d, i, 0);
				g.drawLine(i, (int) d + h1, i, h1);
			}

		}

	}
   
    /*
     * 获得颜色
     */
    private Color getRandColor(int fc,int bc){
        if(fc > 255)
            fc = 255;
        if(bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc-fc-16);
        int g = fc + random.nextInt(bc-fc-14);
        int b = fc + random.nextInt(bc-fc-18);
        return new Color(r,g,b);
    }
}
