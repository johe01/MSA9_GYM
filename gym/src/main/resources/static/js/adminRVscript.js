document.addEventListener("DOMContentLoaded", function () {

  const trainerList = document.getElementById("trainerList")
  trainerList.addEventListener("change", () => {
    const selectedTrainer = trainerList.value;
  })


  var calendarEl = document.getElementById("calendar");
  
  var formattedEvents = reservationEvents.map(event => ({
    title: event.title,
    start: event.start,
    end: event.end,
    description: event.description,
    color: event.color,
    textColor: event.textColor,
    type: event.type,
    display: "block"
    }));

    console.log(formattedEvents);
    var calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: "dayGridMonth",
      dateClick: function (info) {
        showTimeSelectionModal(info.dateStr);
      },
      events: formattedEvents,
      displayEventTime: false,
      dayMaxEventRows: 3,
      eventDidMount: function(info) {
          var eventEl = info.el;
          eventEl.style.cursor = 'pointer';

          var event = info.event;
          var count = event.title.split(' ')[0];
          var date = info.event.startStr.slice(0, 10);
        
          var dateCell = document.querySelector('.fc-day[data-date="' + date + '"]');
          if (dateCell) {
              var countByDateDiv = document.createElement('div');
              countByDateDiv.classList.add('reservation-count');
              countByDateDiv.textContent = count;
              dateCell.appendChild(countByDateDiv);
            }
          },
          eventClick: function (info) {
            window.location.href = '/user/schedule/plan';
          }
        });
        
        calendar.render();
    });
      
      
      
      function showTimeSelectionModal(selectedDate) {
        var modal = document.getElementById("timeSelectionModal");
        var dateDisplay = document.getElementById("selectedDate");
        
        dateDisplay.textContent = `${selectedDate}`;
        
        
        modal.style.display = "block";
      }
      
function closeModal() {
  var modal = document.getElementById("timeSelectionModal");
  modal.style.display = "none";
}

function selectTime(selectedDate, selectedTime) {
  if (confirm(`${selectedDate} ${selectedTime} 예약하시겠습니까?`)) {
    submitReservation(selectedDate, selectedTime);
  }
}

function submitReservation(selectedDate, selectedTime) {
  const [year, month, day] = selectedDate.split("-").map(Number);
  const [hour, minute] = selectedTime.split(":").map(Number);

  const rvDate = new Date(year, month - 1, day, hour, minute);

  const formattedDate = formatDate(rvDate);

  document.getElementById("rvDateInput").value = formattedDate;

  document.getElementById("reservationForm").submit();
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hour = String(date.getHours()).padStart(2, '0');
  const minute = String(date.getMinutes()).padStart(2, '0');
  const second = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}

