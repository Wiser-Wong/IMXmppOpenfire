package com.anch.wxy_pc.imclient.utils;

import com.anch.wxy_pc.imclient.bean.ConversationBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Java 工具类
 *
 * @param <T>
 * @author wxy
 */
public class JavaTools<T> {
    /**
     * 判断一个集合中是否存在一个字符串
     *
     * @param name 字符串
     * @param list 集合
     * @return
     */
    public boolean isExistSubset(T name, List<T> list) {
        return list != null && list.contains(name);
    }

    /**
     * 判断两个集合是否相同
     *
     * @param listOne
     * @param listTwo
     * @return
     */
    public boolean isTwoListSame(List<T> listOne, List<T> listTwo) {
        return !(listOne == null || listTwo == null) && listOne.containsAll(listTwo);
    }

    /**
     * 返回该对象在集合中位置坐标从0开始
     *
     * @param name
     * @param list
     * @return 返回-1表示不存在，默认是从0开始
     */
    public int listIndex(T name, List<T> list) {
        return list.indexOf(name);
    }

    /**
     * 返回该对象在集合中位置坐标从末尾开始倒数 它和indexOf的作用是一样的。
     *
     * @param name
     * @param list
     * @return
     */
    public int listLastIndex(T name, List<T> list) {
        return list.lastIndexOf(name);
    }

    /**
     * set方法是在集合中指定的位置上插入一个数据
     *
     * @return
     */
    public List<T> insertObj(int pos, T name, List<T> list) {
        list.set(pos, name);
        return list;
    }

    /**
     * 求两个集合的差集
     *
     * @param listOne
     * @param listTwo
     * @return
     */
    public List<T> subtractValue(List<T> listOne, List<T> listTwo) {

        List<T> oneTemp = new ArrayList<>();
        List<T> twoTemp = new ArrayList<>();
        oneTemp.addAll(listOne);
        twoTemp.addAll(listTwo);
        List<T> subtractTemp = new ArrayList<>();
        subtractTemp.addAll(oneTemp);
        subtractTemp.addAll(twoTemp);
        oneTemp.retainAll(twoTemp);
        subtractTemp.removeAll(oneTemp);

        return subtractTemp;
    }

    /**
     * 求两个集合的交集
     *
     * @param listOne
     * @param listTwo
     * @return
     */
    public List<T> intersectionValue(List<T> listOne, List<T> listTwo) {
        listOne.retainAll(listTwo);
        return listOne;
    }

    /**
     * 求两个集合的并集
     *
     * @param listOne
     * @param listTwo
     * @return
     */
    public List<T> sumValue(List<T> listOne, List<T> listTwo) {
        listOne.removeAll(listTwo);
        listOne.addAll(listTwo);
        return listOne;
    }

    /**
     * 去掉集合中重复值
     *
     * @param objList
     * @return
     */
    public List<T> resetValue(List<T> objList) {
        Set s = new HashSet(objList);
        objList.clear();
        objList.addAll(s);
        return objList;
    }

    /**
     * 升序
     *
     * @param list
     * @return
     */
    public List<String> upSortList(List<String> list) {
        if (list != null && list.size() > 0)
            Collections.sort(list);
        return list;
    }

    /**
     * 降序
     *
     * @param list
     * @return
     */
    public List<String> downSortList(List<String> list) {
        if (list != null && list.size() > 0)
            Collections.reverse(list);
        return list;
    }

    /**
     * 对象排序
     *
     * @param conversationBeans
     * @return
     */
    public List<ConversationBean> downSort(List<ConversationBean> conversationBeans) {
        if (conversationBeans != null && conversationBeans.size() > 0)
            Collections.sort(conversationBeans, new ComparatorUser());
        else return conversationBeans;
        List<ConversationBean> newConversationBeans = new ArrayList<>();
        for (int i = conversationBeans.size() - 1; i > -1; i--) {
            newConversationBeans.add(conversationBeans.get(i));
        }
        return newConversationBeans;
    }

    public class ComparatorUser implements Comparator {

        public int compare(Object object1, Object object2) {
            ConversationBean conversationBean1 = (ConversationBean) object1;
            ConversationBean conversationBean2 = (ConversationBean) object2;

            return conversationBean1.getDate().compareTo(conversationBean2.getDate());
        }

    }
}
