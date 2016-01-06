public class DialogTrigger extends Box2DActor
{
    private DialogSequence dialog;
    public boolean displayOnce;
    
    public DialogTrigger()
    {  
        super();  
        displayOnce = false;
    }
    
    public DialogTrigger(DialogSequence ds)
    {  
        super();
        dialog = ds;
        displayOnce = false;
    }

    public void setDialog(DialogSequence ds)
    {  dialog = ds;  }
    
    public DialogSequence getDialog()
    {  return dialog;  }
}