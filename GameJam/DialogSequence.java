import java.util.ArrayList;
import java.util.Iterator;

public class DialogSequence
{
    public ArrayList<String> textList;
    public Iterator<String>  textIterator;
    
    public DialogSequence()
    {
        textList = new ArrayList<String>();
    }
    
    public DialogSequence(String... text)
    {
        textList = new ArrayList<String>();
        for (String t : text)
        {
            textList.add(t);
        }
    }
    
    public void add(String t)
    {  textList.add(t);  }
    
    public void initialize()
    {  textIterator = textList.iterator();  }
    
    public String next()
    {  return textIterator.next();  }
    
    public boolean hasNext()
    {  return textIterator.hasNext();  }
    
}