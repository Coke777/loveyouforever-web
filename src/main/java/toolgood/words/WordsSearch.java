package toolgood.words;

import com.cronutils.utils.StringUtils;
import toolgood.words.internals.TrieNode2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class WordsSearch{
    protected TrieNode2[] _first = new TrieNode2[Character.MAX_VALUE + 1];

    /**
     * 设置关键字
     * @param keywords
     */
    public void SetKeywords(List<String> keywords)
    {
        Map<String, Integer> dict = new Hashtable<String, Integer>();
        for (int i = 0; i < keywords.size(); i++) {
            String item=keywords.get(i);
            dict.put(item, i);
        }
        SetKeywords(dict);
    }
    /**
     * 设置关键字
     * @param _keywords
     */
    public void SetKeywords(Map<String, Integer> _keywords)
    {
        TrieNode2[] first =  new TrieNode2[Character.MAX_VALUE + 1];
        TrieNode2 root = new TrieNode2();

        _keywords.forEach((p,index)->{
            if(StringUtils.isEmpty(p)==false){
                TrieNode2 nd = _first[ p.charAt(0)];
                if (nd == null) {
                    nd = root.Add(p.charAt(0));
                    first[p.charAt(0)] = nd;
                }
                for (int i = 1; i < p.length(); i++) {
                    nd = nd.Add(p.charAt(i));
                }
                nd.SetResults(p,index);
            }
        });

        this._first = first;// root.ToArray();

        Map<TrieNode2, TrieNode2> links = new Hashtable<TrieNode2, TrieNode2>();
        root.m_values.forEach((k,v)->{
            TryLinks(v, null, links);
        });
        links.forEach((k,v)->{
            k.Merge(v, links);
        });
    }

    private void TryLinks(TrieNode2 node, TrieNode2 node2, Map<TrieNode2, TrieNode2> links)
    {
        node.m_values.forEach((Key,Value)->{
            TrieNode2 tn = null;
            if (node2 == null) {
                tn = _first[Key];
                if (tn != null) {
                    links.put(Value, tn);
                }
            } else if (node2.HasKey(Key)) {
                tn = node2.GetValue(Key);
                links.put(Value, tn);
            }
            TryLinks(Value, tn, links);
        });
    }


    /**
     * 在文本中查找第一个关键字
     * @param text 文本
     * @return
     */
    public WordsSearchResult FindFirst(String text)
    {
        TrieNode2 ptr = null;
        for (int i = 0; i < text.length(); i++) {
            Character t=text.charAt(i);
            TrieNode2 tn=null;
            if (ptr == null) {
                tn = _first[t];
            } else {
                if (ptr.HasKey(t) == false) {
                    tn = _first[t];
                }else {
                    tn=ptr.GetValue(t);
                }
            }
            if (tn != null) {
                if (tn.End) {
                    for( String key : tn.Results.keySet()){
                        Integer value= tn.Results.get(key);
                        return new WordsSearchResult(key, i + 1 - key.length(), i, value);
                    }
                }
            }
            ptr = tn;
        }
        return null;
    }
    /**
     * 在文本中查找所有的关键字
     * @param text 文本
     * @return
     */
    public List<WordsSearchResult> FindAll(String text)
    {
        TrieNode2 ptr = null;
        List<WordsSearchResult> list = new ArrayList<WordsSearchResult>();

        for (int i = 0; i < text.length(); i++) {
            Character t=text.charAt(i);
            TrieNode2 tn=null;
            if (ptr == null) {
                tn = _first[t];
            } else {
                if (ptr.HasKey(t) == false) {
                    tn = _first[t];
                }else {
                    tn=ptr.GetValue(t);
                }
            }
            if (tn != null) {
                if (tn.End) {
                    for( String key : tn.Results.keySet()){
                        Integer value= tn.Results.get(key);
                        WordsSearchResult item= new WordsSearchResult(key, i + 1 - key.length(), i, value);
                        list.add(item);
                    }
                }
            }
            ptr = tn;
        }
        return list;
    }


    /**
     * 判断文本是否包含关键字
     * @param text 文本
     * @return
     */
    public boolean ContainsAny(String text)
    {
        TrieNode2 ptr = null;
        for (int i = 0; i < text.length(); i++) {
            Character t=text.charAt(i);
            TrieNode2 tn=null;
            if (ptr == null) {
                tn = _first[t];
            } else {
                if (ptr.HasKey(t) == false) {
                    tn = _first[t];
                }else {
                    tn=ptr.GetValue(t);
                }
            }
            if (tn != null) {
                if (tn.End) {
                    return true;
                }
            }
            ptr = tn;
        }
        return false;
    }
    /**
     * 在文本中替换所有的关键字, 替换符默认为 *
     * @param text 文本
     * @return
     */
    public String Replace(String text){
        return Replace(text,'*');
    }
    /**
     * 在文本中替换所有的关键字
     * @param text 文本
     * @param replaceChar 替换符
     * @return
     */
    public String Replace(String text, Character replaceChar)
    {
        StringBuilder result = new StringBuilder(text);

        TrieNode2 ptr = null;
        for (int i = 0; i < text.length(); i++) {
            Character t=text.charAt(i);
            TrieNode2 tn=null;
            if (ptr == null) {
                tn = _first[t];
            } else {
                if (ptr.HasKey(t) == false) {
                    tn = _first[t];
                }else {
                    tn=ptr.GetValue(t);
                }
            }
            if (tn != null) {
                if (tn.End) {
                    Integer maxLength=0;
                    for(String key : tn.Results.keySet()){
                         if(key.length()>maxLength){
                            maxLength = key.length();
                        }
                    }
                    int start = i + 1 - maxLength;
                    for (int j = start; j <= i; j++) {
                        result.setCharAt(j, replaceChar);
                    }
                }
            }
            ptr = tn;
        }
        return result.toString();
    }

}