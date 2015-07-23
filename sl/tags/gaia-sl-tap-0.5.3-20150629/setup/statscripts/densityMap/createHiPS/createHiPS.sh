java -cp .:./fits.jar CreateHealpix -input $1_density -output public.$1_density.fits
cp public.$1_density.fits ../../../statgraphs/.
rm -rf $HOME/.aladin/Cache/HPX
java -Xmx2000m -jar Aladin.jar  public.$1_density.fits &
sleep 14
ps aux | grep -i "java -Xmx2000m -jar Aladin.jar" | awk {'print $2'} | xargs kill -9
setenv location `echo "$PWD" | sed 's/\//_/g' | sed 's/\./_/g'`
java -jar Aladin.jar -hipsgen pixelCut="0 255 asinh" out=$HOME/.aladin/Cache/HPX/"$location"_public_$1_density_fits/TFIELD1 JPEG PNG
rm -rf ~/webapps/$1HiPS
mkdir ~/webapps/$1HiPS/
cp -ri $HOME/.aladin/Cache/HPX/"$location"_public_$1_density_fits/TFIELD1 ~/webapps/$1HiPS
mkdir ~/webapps/$1HiPS/WEB-INF
cd ~/webapps/$1HiPS
tar cvf $1.tar *
cd ~/createHealpix/
mv  ~/webapps/$1HiPS/$1.tar .
scp $1.tar geaops@geadev.esac.esa.int:/var/www/html/
ssh geaops@geadev.esac.esa.int "cd /var/www/html; /var/www/html/create.sh $1"
