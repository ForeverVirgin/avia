import com.google.gson.Gson;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class    avia {
    public static List<Ticket> readJson(String path) throws FileNotFoundException
    {
        Gson g = new Gson();
        File f = new File(path);
        Tickets ticketList;
        Scanner in = new Scanner(f);
        if (f.exists())
        {
            String text = "";
            while(in.hasNextLine()){
                text+=in.nextLine();
            }
            in.close();
            ticketList = g.fromJson(text, Tickets.class);
        }
        else
        {
            in.close();
            throw new FileNotFoundException();
        } 
        return ticketList.tickets;
    }

    public static Integer countDateDifference(String dep_date,String dep_time,String arr_date,String arr_time) throws ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yy");
        String[] splitted_dep_time = dep_time.split(":");
        String[] splitted_arr_time = arr_time.split(":");
        LocalTime dep_time_new = LocalTime.of(Integer.parseInt(splitted_dep_time[0]), Integer.parseInt(splitted_dep_time[1]), Integer.parseInt("0"));
        LocalTime arr_time_new = LocalTime.of(Integer.parseInt(splitted_arr_time[0]), Integer.parseInt(splitted_arr_time[1]), Integer.parseInt("0"));
        Date dep_date_new = myFormat.parse(dep_date);
        Date arr_date_new = myFormat.parse(arr_date);
        int diffInDays = (int)( (arr_date_new.getTime() - dep_date_new.getTime() + arr_time_new.toSecondOfDay() - dep_time_new.toSecondOfDay()));
        return diffInDays;
    }

    public static Map<String,Integer> findMinimalTimes(List<Ticket> ticketList, String origin, String destination) throws ParseException {
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (Ticket i: ticketList)
        {
            if (i.origin.equals(origin) && i.destination.equals(destination))
            {
                Integer dateDifference = countDateDifference(i.departure_date, i.departure_time, i.arrival_date, i.arrival_time);
                if(dictionary.keySet().contains(i.carrier))
                {
                    if(dictionary.get(i.carrier) > dateDifference)
                    {
                        dictionary.put(i.carrier, dateDifference);
                    }
                }
                else
                {
                    dictionary.put(i.carrier, dateDifference);
                }
            }
        }
        return dictionary;
    }

    public static double average_median(List<Ticket> ticketList, String origin, String destination)
    {
        int sum = 0;
        List<Integer> pricelist = new ArrayList<Integer>();
        for (Ticket i: ticketList)
        {
            if (i.origin.equals(origin) && i.destination.equals(destination))
            {
                sum+=i.price;
                pricelist.add(i.price);
            }
        }
        Collections.sort(pricelist);
        double median;
        if (pricelist.size() % 2 == 0)
        {
            median = (pricelist.get(pricelist.size()/2)+pricelist.get(pricelist.size()/2-1))/2.;
        }
        else
        {
            median = pricelist.get(pricelist.size()/2);
        }
        return sum/(double)pricelist.size()-median;
    }

    public static String form(Integer time)
    {
        String formed_time = "";
        if(time>=86400)
        {
            formed_time += time/86400 + " дней ";
            time %=86400;
        }
        if(time>=3600)
        {
            formed_time += time/3600 + " часов ";
            time %=3600;
        }
        if(time>=60)
        {
            formed_time += time/60 + " минут ";
            time %=60;
        }
        if (time >= 0)
        {
            formed_time += time + " секунд ";
        }
        return formed_time;
    }

    public static String help()
    {
        return "Введите 'help', чтобы вызвать эту подсказку.\nВведите 'task1', чтобы получить минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика.\nВведите 'task2', чтобы получить разницу между средней ценой и медианой для полета между городами Владивосток и Тель-Авив.\nВведите 'stop', чтобы закончить работу программы.";
    }
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        System.out.println(help());
        List<Ticket> ticketList = readJson("tickets.json");
        Scanner console_in = new Scanner(System.in);
        while(true)
        {
            String command = console_in.nextLine();
            if(command.equals("help"))
            {
                System.out.println(help());
            }
            else if(command.equals("stop"))
            {
                System.out.println("Остановка...");
                console_in.close();
                return;
            }
            else if(command.equals("task1"))
            {
                System.out.println("Идет подсчет...");
                Map<String,Integer> minimal_times = findMinimalTimes(ticketList, "VVO", "TLV");
                for(String a: minimal_times.keySet())
                {
                    String formed_time = form(minimal_times.get(a));
                    System.out.println(a + ": " + formed_time);
                }
            }
            else if(command.equals("task2"))
            {
                System.out.println("Идет подсчет...");
                double result2 = average_median(ticketList, "VVO", "TLV");
                String insert_string = result2<0 ? "меньше":"больше";
                result2 = Math.abs(result2);
                System.out.println("Средняя цена "+insert_string+" медианы на "+(int)(result2)+ " рублей, "+(int)(result2-(int)result2)*100+" копеек.");
            }
            else
            {
                System.out.println("Команда не найдена.");
            }
        }
    }

}

class Tickets
{
    public List<Ticket> tickets;
}

class Ticket
{
    public String origin;
    public String origin_name;
    public String destination;
    public String destination_time;
    public String departure_date;
    public String departure_time;
    public String arrival_date;
    public String arrival_time;
    public String carrier;
    public int stops;
    public int price;
}