package com.rongzer.efapiao.controller;

import com.rongzer.efapiao.service.InvoiceContentService;
import com.rongzer.rdp.auth.system.util.Logger;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.web.domain.system.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 发票内容
 *
 * @author heps
 * @create 2017-06-01 10:48
 **/
@Controller
@RequestMapping("invoiceContent")
public class InvoiceContentController {
    private static final Logger log = Logger.getLogger(InvoiceContentController.class);

    @Autowired
    private InvoiceContentService invoiceContentService;
    /**
     * 把文件的内容导入数据库
     * @param file 导入的文件
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "insertData", method = { RequestMethod.POST,
            RequestMethod.GET })
    public String importData(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, ModelMap model) {
        String fileName = file.getOriginalFilename();//本次上传的文件名称
        String path1 = request.getSession().getServletContext().getRealPath("/jsp");
        File targetFile = new File(path1, fileName);
        String errorInfo ="";
        String userId = ((LoginUser) request.getSession().getAttribute("LoginUser")).getLoginUserId();
        try {
            file.transferTo(targetFile);
            String path = targetFile.getPath();
            fileName = StringUtil.getUuid32()+fileName;
            errorInfo=invoiceContentService.readExcel(path,fileName,userId);

            model.put("file", fileName);
            if (StringUtil.isEmpty(errorInfo)){
                targetFile.delete();
                errorInfo = " 数据读取成功 ！请关闭弹出层";
                model.put("flag", errorInfo);
                log.info(errorInfo);
            } else {
                targetFile.delete();
                model.put("wrong", errorInfo);
                log.info(errorInfo);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
            errorInfo = "请检查导入文件否正确  ？！";
            model.put("error", errorInfo);
            log.error(errorInfo);
        }catch (Exception e) {
            e.printStackTrace();
            errorInfo = " 请检查导入文件是否正确 ！";
            model.put("error", errorInfo);
            log.error(errorInfo);
        }
        model.put("resultinfo",errorInfo);
        return "rdp/invoice/invoice-content-import";
    }
}
