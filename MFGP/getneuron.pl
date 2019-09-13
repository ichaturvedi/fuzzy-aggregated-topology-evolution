for($i=1; $i<101; $i++)
{
 open(PARA, "../ecg_nn/population1/paras$i.txt");
 $paras = <PARA>;
 close(PARA);

 @list = ();
 @list = split ",",$paras;
 $neu[$i]  = $list[0]*$list[4];
}

open(FILE, "bestTree1.txt");
$nonum = 0;
while($line = <FILE>)
{
  if(index($line,"f")>=0)
  {
    @list = ();
    @list = split " ",$line;
    for($i=0; $i<scalar(@list); $i++)
    {
      if(index($list[$i],"f")>=0 && index($list[$i],"-1")<0)
      {
         $ind = substr($list[$i],1);
         $neu  = $neu[$ind];
         $nonum = $nonum + $neu;
      }
    }
  }

  if(index($line, "new")>=0)
  {
    if($nonum > 0)
    {
      print "$nonum \n";
    }
    $nonum = 0;
  }

}
close(FILE);