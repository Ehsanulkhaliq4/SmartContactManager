console.log("This is Script File");

const toggleSideBar=()=>{

    if($(".sidebar").is(":visible"))
    {
        //true
        //band krna ha 
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }else{

        //false
        //show krna ha
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
};
 const search = () => {
        // console.log("Seaching....");
        let query=$("#search-input").val()
        
        if(query=='')
        {
            $(".search-result").hide();
        }else{
            //search karana ha 
            console.log(query);
            //Sending request to server
            let url=`http://localhost:8080/search/${query}`
            fetch(url).then((response) => {
                return response.json();
            }).then((data) => {
                //data dekhana ka liya
                console.log(data);

                let text=`<div class='list-group'>`;

                data.forEach((contact) => {
                    text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
                })

                text+='</div>';
                $(".search-result").html(text);
                $(".search-result").show();
            })
            
        }
 };
 //First request - to server to create order

        const paymentStart=()=>{
        console.log("Payment Started...");
        let amount=$("#payment_feild").val();
        console.log(amount);
        if(amount == "" || amount == null)
        {
            alert("Amount is Required!!");
            return;
        }
        //Agr hum na shi amount di to ya code run hoga
        //1: yhan hamein server pa request bhejni ha order create krna ka liya
        //we will use ajax to send request to server to create order
            $.ajax(
                {
                    url:'/user/create_order',
                    data:JSON.stringify({amount:amount,infi:'order_request'}),
                    contentType:'application/json',
                    type:'POST',
                    success:function(response){
                        //invoke when success
                        console.log(response);
                    },
                    error:function(error){
                        //invoke when error
                        console.log(error)
                        alert('Somthing went wrong....');
                        
                    },
                    
                }
            )
};