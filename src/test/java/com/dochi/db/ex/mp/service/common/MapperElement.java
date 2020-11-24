package com.dochi.db.ex.mp.service.common;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class MapperElement {
    // 변수 설정
    //   - XML Child 객체 변수
    private Element element = null;
    
    // 생성자
    //   - Element를 저장
    public MapperElement(Element element) {
        this.element = element;
    }
    
    // XML 탐색기
    //   - id가 'attrValue'인 SQL 조회
    public String getSQL(String attrValue) {
        return getChild("id", attrValue).getText();
    }
    
    //   - 속성명, 속성값으로 Child 탐색
    public MapperElement getChild(String attrName, String attrValue) {
        return getChild(null, attrName, attrValue);
    }
 
    //   - 이름, 속성명, 속성값으로 Child 탐색
    public MapperElement getChild(String name, String attrName, String attrValue) {
        List<?> children = null;
        
        if( name != null ) {
            children = this.element.getChildren(name);
        } else {
            children = this.element.getChildren();
        }
        
        Iterator<?> iter = children.iterator();
        while(iter.hasNext()) {
            Element element = (Element) iter.next(); 
            String elementAttrValue = element.getAttributeValue(attrName);
            
            // Element의 속성값을 비교하여 일치하는 Element 추출
            //   - 추출된 Element는 MapperElement 객체로 생성하여 반환
            if( elementAttrValue != null && elementAttrValue.equalsIgnoreCase(attrValue) ) {
                return new MapperElement(element);
            }
        }
        
        return null;
    }
    
    /**
     * org.jdom.Element 기본 함수 정의 
     */
    public String getText() {
        return this.element.getText();
    }
    public List<?> getChildren() {
        return this.element.getChildren();
    }
    public List<?> getChildren(String name) {
        return this.element.getChildren(name);
    }
    public String getAttributeValue(String attrName) {
        return this.element.getAttributeValue(attrName);
    }
    public String getAttributeValue(String attrName, String defaultValue) {
        return this.element.getAttributeValue(attrName, defaultValue);
    }
}