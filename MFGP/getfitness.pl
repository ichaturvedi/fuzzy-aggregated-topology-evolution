open(FILE, $ARGV[0]);
while($line = <FILE>)
{
 @list = ();
 @list = split " ",$line;
 print $list[5]."\n";
}
close(FILE);