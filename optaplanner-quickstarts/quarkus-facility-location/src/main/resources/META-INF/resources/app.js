const colors = [
  'aquamarine',
  'blueviolet',
  'chocolate',
  'cornflowerblue',
  'crimson',
  'forestgreen',
  'gold',
  'lawngreen',
  'orange',
  'tomato',
];

const get = () => {
  fetch('http://localhost:8080/flp/get')
    .then(response => response.json())
    .then(data => showProblem(data));
};

const showProblem = (problem) => {
  map.fitBounds(problem.bounds);
  const groups = new Map();
  problem.facilities.forEach((facility, index) => {
    L.marker(facility.location).addTo(map);
    groups.set(facility.id, colors[index % colors.length]);
  });

  problem.demandPoints.forEach(dp => L.circleMarker(dp.location).addTo(map));
  problem.demandPoints
    .filter(dp => dp.facility !== null)
    .forEach(dp => L.polyline([dp.location, dp.facility.location], { color: groups.get(dp.facility.id) }).addTo(map));
};

const map = L.map('map').setView([51.505, -0.09], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 19,
  attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
}).addTo(map);

map.on('click', get);
