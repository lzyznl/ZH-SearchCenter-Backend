package com.yupi.springbootinit.Test;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class SeleniumTest {


    @Test
    void seleniumTest(){
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        //System.setProperty("webdriver.chrome.driver", "D:\\chrome\\edgedriver_win64\\msedgedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 启用无头模式

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.bing.com/?toWww=1&redig=067AB329E7F54B8A94BA1174C4A37A6B");
        //爬取bing的数据
        WebElement inputForm = driver.findElement(By.className("sb_form_q"));
        inputForm.sendKeys("五月天2023演唱会");
        inputForm.submit();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.titleContains("五月天2023演唱会"));
        WebElement urlElement = driver.findElement(By.className("sb_pagF"));
        List<WebElement> a = urlElement.findElements(By.cssSelector("a"));
        for(int i=0;i<a.size();++i){
            WebElement webElement = a.get(i);
            if(webElement.getText().equals("1")){
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", webElement);
                break;
            };
        }
//        WebDriverWait waits = new WebDriverWait(driver, Duration.ofSeconds(5));
//        waits.until(ExpectedConditions.urlContains("&first=11"));
        //System.out.println(driver.getPageSource());
        List<WebElement> liElement = driver.findElements(By.className("b_algo"));
        for(int i = 0; i < liElement.size(); ++i){
            WebElement element = liElement.get(i);
            WebElement pElement = element.findElement(By.cssSelector("p"));
            WebElement aElement = element.findElement(By.cssSelector("a"));
            WebElement wUrlElement = element.findElement(By.className("b_attribution"));
            WebElement citeElement = wUrlElement.findElement(By.cssSelector("cite"));
            WebElement imgElement=null;
            try {
                imgElement = element.findElement(By.cssSelector("img"));
            }catch(Exception e){
                String src=null;
                if(imgElement!=null){
                    src = imgElement.getAttribute("src");
                }
                String description = pElement.getText();
                String url = citeElement.getText();
                String text = aElement.getText();
                String href = aElement.getAttribute("href");

                System.out.println(text);
                System.out.println(href);
                System.out.println(url);
                System.out.println(src);
                System.out.println(description);
                System.out.println();
                continue;
            }
        }

        driver.quit();
    }

    @Test
    void test(){
        String pageUrl = "https://www.bing.com/search?q=%e4%ba%94%e6%9c%88%e5%a4%a92023%e6%bc%94%e5%94%b1%e4%bc%9a&FPIG=E2ED7953EB0147B497AE436D1DDE0D39&first=11&FORM=PERE";

        String pattern = "first=(\\d+)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(pageUrl);

        if (matcher.find()) {
            String firstValue = matcher.group(1);
            System.out.println("First value: " + firstValue);
        } else {
            System.out.println("First value not found.");
        }
    }
}
